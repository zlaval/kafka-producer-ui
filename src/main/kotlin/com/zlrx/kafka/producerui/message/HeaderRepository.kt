package com.zlrx.kafka.producerui.message

import com.zlrx.kafka.producerui.domain.Header
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Transactional(propagation = Propagation.MANDATORY)
interface HeaderRepository : CrudRepository<Header, String> {
}