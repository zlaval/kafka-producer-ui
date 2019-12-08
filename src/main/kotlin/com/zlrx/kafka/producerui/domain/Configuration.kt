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
    val connection: Connection,

    @OneToOne(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(name = "message_id")
    val message: Message? = null,

    @OneToOne(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(name = "topic_id")
    val topic: Topic? = null,

    @Column(name = "default_config")
    val default: Boolean = false

) : BaseEntity()