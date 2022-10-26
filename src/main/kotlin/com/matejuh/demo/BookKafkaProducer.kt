package com.matejuh.demo

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class BookKafkaProducer(
    private val objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    fun publish(event: BookUpdated) {
        kafkaTemplate.send(
            "event.books",
            event.id.toString(),
            objectMapper.writeValueAsString(event)
        ).get(10, TimeUnit.SECONDS)
    }
}
