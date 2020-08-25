package com.zlrx.kafka.producerui.service

import com.zlrx.kafka.producerui.message.FilePath
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

const val BATCH_SIZE = 50000L

@Service
class FileHandlerService constructor(
    @Value("\${json.file.directory:}")
    val jsonFileDirectory: String? = null
) {

    fun readFilesFromDir(): List<FilePath> {
        if (jsonFileDirectory.isNullOrBlank()) {
            return emptyList()
        }
        return Files.walk(Paths.get(jsonFileDirectory)).use {
            it.filter { file ->
                Files.isRegularFile(file)
            }.map { file ->
                FilePath(file.fileName.toString(), file.toAbsolutePath().toString())
            }.toList()
        }
    }

    fun getLinesInBatch(path: String, startFrom: Long): List<String> =
        Files.lines(Paths.get(path)).use {
            it.skip(startFrom).limit(BATCH_SIZE).toList()
        }
}
