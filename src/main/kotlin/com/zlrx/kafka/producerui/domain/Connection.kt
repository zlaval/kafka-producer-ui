package com.zlrx.kafka.producerui.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table


@Entity
@Table(name = "connection")
class Connection(

    val name: String,

    val broker: String,

    @Column(name = "schema_registry")
    val schemaRegistry: String

) : BaseEntity()