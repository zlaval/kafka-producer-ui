package com.zlrx.kafka.producerui.message

import com.zlrx.kafka.producerui.service.Producer
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ProducerContainer {

    private val producers = ConcurrentHashMap<ProducerProps, Producer>()

    fun getOrPut(props: ProducerProps): Producer = producers.getOrPut(props) { Producer(props) }

}