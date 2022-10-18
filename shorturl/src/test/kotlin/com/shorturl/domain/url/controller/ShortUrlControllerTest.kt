package com.shorturl.domain.url.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.shorturl.generated.model.MakeShortUrlRequest
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryOneTime
import org.apache.curator.test.TestingServer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShortUrlControllerTest(
    @Autowired private val testRestTemplate: TestRestTemplate,
    @LocalServerPort private val port: Int,
) {

    @BeforeAll
    fun setup() {
        val server = TestingServer(2181)
        val builder: CuratorFrameworkFactory.Builder = CuratorFrameworkFactory.builder()
        val client = builder.connectString(server.connectString).retryPolicy(RetryOneTime(1)).build()
        client.start()
    }

    @Test
    fun shouldReturnCreatedResponseOnValidInput() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val makeShortUrlRequest = MakeShortUrlRequest("http://www.google.com")
        val objectMapper = ObjectMapper()
        val entity = HttpEntity<String>(
            objectMapper.writeValueAsString(makeShortUrlRequest),
            headers
        )
        val response =
            testRestTemplate.exchange(
                createURLWithPort("/v1/url/makeshort"),
                HttpMethod.POST, entity, String::class.java
            )
        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun shouldReturnBadRequestResponseOnInvalidLongURL() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val makeShortUrlRequest = MakeShortUrlRequest("//www.google.com")
        val objectMapper = ObjectMapper()
        val entity = HttpEntity<String>(
            objectMapper.writeValueAsString(makeShortUrlRequest),
            headers
        )
        val response =
            testRestTemplate.exchange(
                createURLWithPort("/v1/url/makeshort"),
                HttpMethod.POST, entity, String::class.java
            )
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    private fun createURLWithPort(uri: String): String {
        return "http://localhost:$port$uri"
    }
}