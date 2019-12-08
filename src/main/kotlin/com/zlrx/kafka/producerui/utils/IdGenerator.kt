package com.zlrx.kafka.producerui.utils

import org.springframework.util.AlternativeJdkIdGenerator

object IdGenerator {

    private val idGenerator = AlternativeJdkIdGenerator()

    fun generateId(): String = idGenerator.generateId().toString().replace("-", "")

}