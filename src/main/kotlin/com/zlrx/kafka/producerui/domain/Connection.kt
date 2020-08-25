package com.zlrx.kafka.producerui.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "connection")
class Connection(

    var name: String,

    var broker: String,

    @Column(name = "schema_registry")
    var schemaRegistry: String

) : BaseEntity()
