package com.zlrx.kafka.producerui.domain

import org.apache.avro.Schema

data class Schemas(
    val name: String,
    val schema: Schema
)
