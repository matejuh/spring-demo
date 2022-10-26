package com.matejuh.demo

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import net.javacrumbs.jsonunit.JsonMatchers.jsonStringEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.PathContainer
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.util.pattern.PathPatternParser

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
    topics = ["event.books"],
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    brokerProperties = [
        "transaction.state.log.replication.factor=1",
        "transaction.state.log.min.isr=1",
        "num.partitions=1"
    ]
)
class BookControllerTest(
    @LocalServerPort localServerPort: Int,
    @Autowired private val testListener: TestListener
) {

    init {
        RestAssured.port = localServerPort
        RestAssured.requestSpecification = RequestSpecBuilder()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @BeforeEach
    internal fun setUp() {
        testListener.reset()
    }

    @Test
    internal fun `should create and get a book`() {
        val location = Given {
            body(createBook)
        } When {
            post(BOOK_URI)
        } Then {
            statusCode(HttpStatus.CREATED.value())
        } Extract {
            header(HttpHeaders.LOCATION)
        }
        val id = PathPatternParser()
            .parse(BOOK_ITEM_URI)
            .matchAndExtract(PathContainer.parsePath(location))
            ?.uriVariables
            ?.get("id")
        id.shouldNotBeNull()

        val updatedEvent = testListener.fetch("event.books")
        updatedEvent.key() shouldBe id

        When {
            get(location)
        } Then {
            body(
                jsonStringEquals(
                    """{
                       "id":"$id",
                       "name":"${createBook.name}",
                       "author":"${createBook.author}"
                    }
                    """.trimIndent()
                )
            )
        }
    }
}
