package com.redirect.domain.url.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.redirect.zookeeper.kafka.config.KafkaTopicConfig.Companion.publicUrlInfoTopic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@DirtiesContext
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedirectUrlControllerTest(
    @Autowired private val testRestTemplate: TestRestTemplate,
    @LocalServerPort private val port: Int,
    @Autowired private val kafkaTemplate: KafkaTemplate<String, String>
) {

    val objectMapper = ObjectMapper()

    @BeforeAll
    fun setup() {
        MockitoAnnotations.openMocks(this)
        kafkaTemplate.send(
            publicUrlInfoTopic,
            objectMapper.writeValueAsString("123456"),
            objectMapper.writeValueAsString("http://www.google.com")
        )
    }

    @Test
    fun shouldReturnFoundWhenLongUrlIsFound() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity<String>(headers)
        val response =
            testRestTemplate.exchange(
                createURLWithPort("/v1/123456"),
                HttpMethod.GET, entity, String::class.java
            )
        Assertions.assertEquals(HttpStatus.FOUND, response.statusCode)
    }

    @Test
    fun shouldReturnNotFoundWhenLongUrlIsNotFound() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity<String>(headers)
        val response =
            testRestTemplate.exchange(
                createURLWithPort("/v1/999"),
                HttpMethod.GET, entity, String::class.java
            )
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    private fun createURLWithPort(uri: String): String {
        return "http://localhost:$port$uri"
    }
}