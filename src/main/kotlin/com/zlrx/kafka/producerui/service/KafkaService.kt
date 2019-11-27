package com.zlrx.kafka.producerui.service

import com.zlrx.kafka.producerui.message.MessageData
import com.zlrx.kafka.producerui.message.Producer
import com.zlrx.kafka.producerui.message.ProducerContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KafkaService @Autowired constructor(val container: ProducerContainer) {

    //v2 validate messageData
    fun sendMessage(messageData: MessageData) {
        val producer = container.getOrPut(messageData.props, Producer(messageData.props));
        producer.produce(messageData)
    }

}