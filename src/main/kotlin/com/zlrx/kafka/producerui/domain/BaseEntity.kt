package com.zlrx.kafka.producerui.domain

import com.zlrx.kafka.producerui.utils.IdGenerator
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
abstract class BaseEntity {

    @Id
    val id: String = IdGenerator.generateId()

    @Version
    var version: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}