package com.redirect.zookeeper.kafka.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.redirect.zookeeper.kafka.config.KafkaTopicConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StateStoreQueryServiceTest(
    @Autowired private val kafkaTemplate: KafkaTemplate<String, String>,
    @Autowired val stateStoreQueryService: StateStoreQueryService
) {
    val objectMapper = ObjectMapper()

    @BeforeAll
    fun setup() {
        MockitoAnnotations.openMocks(this)
        kafkaTemplate.send(
            KafkaTopicConfig.publicUrlInfoTopic,
            objectMapper.writeValueAsString("123456"),
            objectMapper.writeValueAsString("http://www.google.com")
        )
    }

    @Test
    fun shouldReturnValidValueForValidKey() {
        val expected = "http://www.google.com"
        val actual = stateStoreQueryService.findValueforKey("123456")
        assertEquals(expected, actual)
    }

    @Test
    fun shouldReturnNullForInValidKey() {
        val actual = stateStoreQueryService.findValueforKey("99")
        assertNull(actual)
    }

}