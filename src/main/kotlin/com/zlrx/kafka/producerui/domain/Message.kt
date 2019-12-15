package com.zlrx.kafka.producerui.domain

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Lob
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "message")
class Message(
    val key: String? = null,

    @Lob
    val text: String? = null,

    val file: Boolean = false,

    @Column(name = "file_path")
    val filePath: String? = null,

    @Column(name = "file_name")
    val fileName: String? = null,

    @OneToMany(mappedBy = "message", cascade = [CascadeType.ALL], orphanRemoval = true)
    var headers: MutableList<Header> = mutableListOf()
) : BaseEntity()