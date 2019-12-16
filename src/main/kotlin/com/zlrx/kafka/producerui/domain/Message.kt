package com.zlrx.kafka.producerui.domain

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Lob
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "message")
class Message(
    var key: String? = null,

    @Lob
    var text: String? = null,

    var file: Boolean = false,

    @Column(name = "file_path")
    var filePath: String? = null,

    @Column(name = "file_name")
    var fileName: String? = null,

    @OneToMany(mappedBy = "message", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var headers: MutableList<Header> = mutableListOf()
) : BaseEntity() {

    fun copy(): Message {
        val messageCopy = Message(key, text, file, filePath, fileName, mutableListOf())
        val headersCopy: List<Header> = headers.map {
            it.copy(messageCopy)
        }
        messageCopy.headers.addAll(headersCopy)
        return messageCopy
    }
}