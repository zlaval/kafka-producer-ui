package com.zlrx.kafka.producerui.domain

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "configuration")
class Configuration(

    var name: String,

    @OneToOne
    @JoinColumn(name = "connection_id")
    var connection: Connection,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "message_id")
    var message: Message,

    @OneToOne
    @JoinColumn(name = "topic_id")
    var topic: Topic,

    @Column(name = "default_config")
    var default: Boolean = false

) : BaseEntity() {

    fun copy(name: String): Configuration {
        val messageCopy = message.copy()
        return Configuration(name, connection, messageCopy, topic)
    }
}
