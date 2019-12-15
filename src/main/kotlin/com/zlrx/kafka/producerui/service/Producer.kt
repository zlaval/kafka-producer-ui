package com.zlrx.kafka.producerui.service

import com.zlrx.kafka.producerui.message.MessageData
import com.zlrx.kafka.producerui.message.ProducerProps
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

//v2: kafka server should be parameterized
//v2: serializer pick on ui
class Producer(props: ProducerProps) {

    private val properties: Properties = Properties()
    private val producer: KafkaProducer<String, String>

    init {
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.bootStrapServer)
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.qualifiedName)
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.qualifiedName)
        properties.setProperty(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, props.schemaRegistryUrl)
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, (32 * 1024).toString())
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20")
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "1")
        producer = KafkaProducer(properties)
    }

    //FIXME if cant connect throw error not try forever
    fun produce(messageData: MessageData) {
        val headers = messageData.headers?.map {
            RecordHeader(it.key, it.value.toByteArray())
        }
        val record = ProducerRecord(messageData.topic, null, messageData.key, messageData.message, headers)
        producer.send(record).get()
    }
}