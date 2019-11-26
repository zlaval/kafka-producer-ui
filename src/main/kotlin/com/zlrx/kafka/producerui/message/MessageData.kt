package com.zlrx.kafka.producerui.message

data class Header(
    val key: String,
    val value: String
)

//v2: key,value generic
data class MessageData(
    val topic: String,
    val key: String?,
    val message: String,
    val headers: List<Header>?,
    val props: ProducerProps
)