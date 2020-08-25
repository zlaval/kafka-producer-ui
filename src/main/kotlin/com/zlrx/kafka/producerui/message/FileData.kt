package com.zlrx.kafka.producerui.message

import com.zlrx.kafka.producerui.domain.Header

data class FileData(
    val topic: String,
    val key: String?,
    val filePath: FilePath,
    val headers: List<Header>?,
    val props: ProducerProps
)
