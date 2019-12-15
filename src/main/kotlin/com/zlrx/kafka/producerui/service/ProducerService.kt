package com.zlrx.kafka.producerui.service

import com.zlrx.kafka.producerui.domain.Configuration
import com.zlrx.kafka.producerui.domain.Connection
import com.zlrx.kafka.producerui.repository.ConfigurationRepository
import com.zlrx.kafka.producerui.repository.ConnectionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProducerService @Autowired constructor(
    val connectionRepository: ConnectionRepository,
    val configurationRepository: ConfigurationRepository
) {

    fun loadConnections(): List<Connection> = connectionRepository.findByOrderByName()

    fun saveConnection(name: String, broker: String, schema: String): Connection {
        return connectionRepository.save(Connection(name, broker, schema))
    }

    fun loadDefaultConfiguration(): Configuration? = configurationRepository.findByDefaultTrue()

}