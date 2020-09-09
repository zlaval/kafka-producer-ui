package com.zlrx.kafka.producerui.message

import com.zlrx.kafka.producerui.domain.Header
import org.apache.avro.Schema

data class MessageData(
    val topic: String,
    val key: String?,
    val message: String,
    val headers: List<Header>?,
    val props: ProducerProps,
    val schema: Schema?
)
