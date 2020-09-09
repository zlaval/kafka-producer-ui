package com.zlrx.kafka.producerui.service

import com.zlrx.kafka.producerui.domain.Schemas
import org.apache.avro.Schema
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SchemaCollector {

    fun getSchemasFromRegistry(url: String?): List<Schemas> {
        if (url.isNullOrBlank()) {
            return emptyList()
        }
        return try {
            val subjects = getSubjects(url)
            val schemas = subjects?.map {
                getForSchema(it, url)
            }?.map {
                Schema.parse(it)
            }?.map { Schemas(it.name, it) }

            schemas ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getForSchema(name: String, url: String): String? {
        val restTemplate = RestTemplate()
        return restTemplate.getForEntity("$url/subjects/$name/versions/1/schema", String::class.java).body
    }

    fun getSubjects(url: String): Array<String>? {
        val restTemplate = RestTemplate()
        return restTemplate.getForEntity("$url/subjects", Array<String>::class.java).body
    }
}
