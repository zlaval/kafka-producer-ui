package com.zlrx.kafka.producerui.message

import org.springframework.stereotype.Component

@Component
class ProducerContainer {

    private val producers = HashMap<ProducerProps, Producer>()

    fun getOrPut(props: ProducerProps, default: Producer): Producer = producers.getOrPut(
        props) { default }

}