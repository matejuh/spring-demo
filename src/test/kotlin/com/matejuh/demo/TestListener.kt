package com.matejuh.demo

import mu.KLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

@Component
class TestListener {
    private val records: ConcurrentMap<String, BlockingQueue<ConsumerRecord<Any, Any>>> = ConcurrentHashMap()

    @Volatile
    private var lastResetTime: Long = now()

    @KafkaListener(id = "test-listener", topicPattern = ".*")
    fun listen(record: ConsumerRecord<Any, Any>) {
        val recordTimestamp = record.timestamp()

        // If a record was created before the reset time, it most likely belongs to an already finished test
        if (recordTimestamp < lastResetTime) {
            logger.info { "action=ignored recordTimestamp=$recordTimestamp lastResetTime=$lastResetTime $record" }
            return
        }
        logger.info { "action=received $record" }
        queueFor(record.topic()).put(record)
    }

    private fun queueFor(topic: String) = records.computeIfAbsent(topic) { LinkedBlockingDeque() }

    fun fetch(topic: String): ConsumerRecord<Any, Any> =
        queueFor(topic).poll(5, TimeUnit.SECONDS) ?: error("Record did not arrive")

    fun reset() {
        lastResetTime = now()
        records.clear()
    }

    /**
     * Using System.currentTimeMillis() as that's what Kafka producer is using.
     */
    private fun now(): Long = System.currentTimeMillis()

    companion object : KLogging()
}
