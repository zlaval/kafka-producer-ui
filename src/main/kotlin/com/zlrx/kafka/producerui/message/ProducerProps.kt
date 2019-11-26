package com.zlrx.kafka.producerui.message


data class ProducerProps(
    val bootStrapServer: String = "kafka:9092",
    val schemaRegistryUrl: String = "schema-registry:8081"
)