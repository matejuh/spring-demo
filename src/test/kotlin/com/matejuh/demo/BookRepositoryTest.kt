package com.matejuh.demo

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@JooqTest
class BookRepositoryTest(@Autowired jooq: DSLContext) {

    private val repository: BookRepository = BookRepository(jooq)

    @Test
    internal fun `Should store and get`() {
        val id = repository.create(createBook)
        id.shouldNotBeNull()
        val book = repository.get(id)
        book.shouldNotBeNull()
        book.id shouldBe id
        book.name shouldBe createBook.name
        book.author shouldBe createBook.author
    }
}
