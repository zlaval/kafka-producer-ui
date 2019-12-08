package com.zlrx.kafka.producerui.domain

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "header")
class Header(
    val key: String,
    val value: String,

    @ManyToOne
    @JoinColumn(name = "message_id")
    val message: Message? = null
) : BaseEntity()