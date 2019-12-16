package com.zlrx.kafka.producerui.domain

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "header")
class Header(

    var key: String,

    var value: String,

    @ManyToOne
    @JoinColumn(name = "message_id")
    var message: Message
) : BaseEntity() {

    fun copy(message: Message): Header = Header(key, value, message)

}