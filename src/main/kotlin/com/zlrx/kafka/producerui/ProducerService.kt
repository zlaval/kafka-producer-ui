package com.zlrx.kafka.producerui

import com.zlrx.kafka.producerui.domain.Connection
import com.zlrx.kafka.producerui.repository.ConnectionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProducerService @Autowired constructor(
    val connectionRepository: ConnectionRepository
) {

    fun loadConnections(): List<Connection> = connectionRepository.findByOrderByName()

}