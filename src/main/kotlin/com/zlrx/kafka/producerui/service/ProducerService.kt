package com.zlrx.kafka.producerui.service

import com.zlrx.kafka.producerui.domain.Configuration
import com.zlrx.kafka.producerui.domain.Connection
import com.zlrx.kafka.producerui.domain.Topic
import com.zlrx.kafka.producerui.message.HeaderRepository
import com.zlrx.kafka.producerui.repository.ConfigurationRepository
import com.zlrx.kafka.producerui.repository.ConnectionRepository
import com.zlrx.kafka.producerui.repository.MessageRepository
import com.zlrx.kafka.producerui.repository.TopicRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
@Service
@Transactional
class ProducerService @Autowired constructor(
    val connectionRepository: ConnectionRepository,
    val configurationRepository: ConfigurationRepository,
    val topicRepository: TopicRepository,
    val headerRepository: HeaderRepository,
    val messageRepository: MessageRepository
) {

    fun loadConnections(): List<Connection> = connectionRepository.findByOrderByName()

    fun loadTopics(): List<Topic> = topicRepository.findByOrderByName()

    fun saveConnection(name: String, broker: String, schema: String): Connection = connectionRepository.save(Connection(name, broker, schema))

    fun saveTopic(name: String, topic: String): Topic = topicRepository.save(Topic(name, topic))

    fun saveConfiguration(configuration: Configuration): Configuration {
        return configurationRepository.save(configuration)
    }

    fun loadDefaultConfiguration(): Configuration = configurationRepository.findByDefaultTrue()
}
