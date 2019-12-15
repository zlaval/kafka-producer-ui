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
    @OneToOne(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(name = "connection_id")
    var connection: Connection,

    @OneToOne(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(name = "message_id")
    var message: Message,

    @OneToOne(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(name = "topic_id")
    var topic: Topic,

    @Column(name = "default_config")
    var default: Boolean = false

) : BaseEntity()