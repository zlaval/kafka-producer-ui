package com.zlrx.kafka.producerui.service

import com.zlrx.kafka.producerui.message.FileData
import com.zlrx.kafka.producerui.message.MessageData
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KafkaService @Autowired constructor(
    val fileHandlerService: FileHandlerService
) {

    private val logger = LoggerFactory.getLogger("KafkaService")

    // v2 validate messageData
    fun sendMessage(messageData: MessageData) {
        val producer = Producer(messageData.props)
        producer.produce(messageData)
    }

    fun sendMessageFromFile(fileData: FileData) {
        val producer = Producer(fileData.props)
        var start = 0L
        logger.info("Start to process messages from file")
        var rows: List<String> = fileHandlerService.getLinesInBatch(fileData.filePath.path, start)
        while (rows.isNotEmpty()) {
            for (line in rows) {
                producer.produce(
                    MessageData(
                        fileData.topic,
                        fileData.key,
                        line,
                        fileData.headers,
                        fileData.props,
                        fileData.schema
                    )
                )
            }
            start += BATCH_SIZE
            rows = fileHandlerService.getLinesInBatch(fileData.filePath.path, start)
            logger.info("The first $start message has been processed, batch size is $BATCH_SIZE")
        }
        logger.info("All messages has been processed from file")
    }
}
