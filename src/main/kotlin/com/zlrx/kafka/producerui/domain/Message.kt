package com.zlrx.kafka.producerui.domain

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Lob
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "message")
class Message(
    val key: String,

    @Lob
    val text: String,

    @OneToMany(mappedBy = "message", cascade = [CascadeType.ALL], orphanRemoval = true)
    var headers: MutableList<Header> = mutableListOf()
) : BaseEntity() {


}