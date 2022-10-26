package com.matejuh.demo

import java.util.UUID

typealias BookId = UUID

data class ApiBook(
    val id: BookId,
    val name: String,
    val author: String
)

data class Book(
    val id: BookId,
    val name: String,
    val author: String
)

data class CreateBook(
    val name: String,
    val author: String
)

data class BookUpdated(
    val id: BookId,
    val name: String,
    val author: String
)
