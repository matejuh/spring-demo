package com.matejuh.demo

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val repository: BookRepository,
    private val kafkaProducer: BookKafkaProducer
) {
    @Transactional
    fun createBook(createBook: CreateBook): BookId =
        repository.create(createBook).also {
            kafkaProducer.publish(BookUpdated(it, createBook.name, createBook.author))
        }

    fun getBook(id: BookId): Book? =
        repository.get(id)
}
