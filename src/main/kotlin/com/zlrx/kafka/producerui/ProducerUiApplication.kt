package com.zlrx.kafka.producerui

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.zlrx")
@SpringBootApplication
class ProducerUiApplication

fun main(args: Array<String>) {
    runApplication<ProducerUiApplication>(*args)
}
