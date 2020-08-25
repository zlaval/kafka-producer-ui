package com.zlrx.kafka.producerui.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "topic")
class Topic(
    var name: String,

    @Column(name = "topic_name")
    var topicName: String

) : BaseEntity()
