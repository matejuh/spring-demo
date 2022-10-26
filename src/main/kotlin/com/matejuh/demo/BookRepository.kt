package com.matejuh.demo

import com.matejuh.demo.generated.tables.Books.Companion.BOOKS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(private val jooq: DSLContext) {
    fun create(createBook: CreateBook): BookId =
        jooq.insertInto(
            BOOKS,
            BOOKS.NAME,
            BOOKS.AUTHOR
        ).values(
            createBook.name,
            createBook.author
        ).returningResult(BOOKS.ID)
            .fetchOne()
            ?.value1()!!

    fun get(id: BookId): Book? =
        jooq.selectFrom(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetchOne()
            ?.let { r -> Book(r[BOOKS.ID]!!, r[BOOKS.NAME]!!, r[BOOKS.AUTHOR]!!) }
}
