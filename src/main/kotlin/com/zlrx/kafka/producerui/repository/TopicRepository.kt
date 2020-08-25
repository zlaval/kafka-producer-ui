package com.zlrx.kafka.producerui.repository

import com.zlrx.kafka.producerui.domain.Topic
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Transactional(propagation = Propagation.MANDATORY)
interface TopicRepository : CrudRepository<Topic, String> {

    fun findByOrderByName(): List<Topic>
}
