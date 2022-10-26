package com.matejuh.demo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

const val BOOK_URI = "/books"
const val BOOK_ITEM_URI = "/books/{id}"

@RestController
class BookController(private val bookService: BookService) {
    @PostMapping(BOOK_URI)
    fun createBook(@RequestBody createBook: CreateBook): ResponseEntity<Nothing> =
        bookService.createBook(createBook).let {
            ResponseEntity.created(UriComponentsBuilder.newInstance().path(BOOK_ITEM_URI).build(it)).build()
        }

    @GetMapping(BOOK_ITEM_URI)
    fun getBook(@PathVariable id: BookId): ApiBook =
        bookService.getBook(id)?.toApi()!!
}

private fun Book.toApi(): ApiBook = ApiBook(id, name, author)
