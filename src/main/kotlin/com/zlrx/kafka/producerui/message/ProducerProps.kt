package com.zlrx.kafka.producerui.message

data class ProducerProps(
    val bootStrapServer: String,
    val schemaRegistryUrl: String
)
