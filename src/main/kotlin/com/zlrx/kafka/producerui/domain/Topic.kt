package com.zlrx.kafka.producerui.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "topic")
class Topic(
    val name: String,

    @Column(name = "topic_name")
    val topicName: String

) : BaseEntity()