package com.zlrx.kafka.producerui.repository

import com.zlrx.kafka.producerui.domain.Message
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Transactional(propagation = Propagation.MANDATORY)
interface MessageRepository : CrudRepository<Message, String> {
}