package com.shorturl.zookeeper.kafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.shorturl.zookeeper.kafka.config.KafkaTopicConfig
import com.shorturl.zookeeper.kafka.model.UrlInfo
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@DirtiesContext
@ExtendWith(SpringExtension::class)
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaUrlInfoProducerTest(
    @Autowired private val kafkaUrlInfoProducer: KafkaUrlInfoProducer
) {

    lateinit var consumerRecords: BlockingQueue<ConsumerRecord<String, String>>
    lateinit var container: KafkaMessageListenerContainer<String, String>

    @BeforeAll
    fun setup() {
        consumerRecords = LinkedBlockingQueue()
        val containerProperties = ContainerProperties(KafkaTopicConfig.publicUrlInfoTopic)

        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ConsumerConfig.GROUP_ID_CONFIG] = "testgroup"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        val consumer = DefaultKafkaConsumerFactory<String, String>(props)
        container = KafkaMessageListenerContainer(consumer, containerProperties)
        container.setupMessageListener(MessageListener<String, String> { record ->
            consumerRecords.add(record)
        })
        container.start()
        ContainerTestUtils.waitForAssignment(container, 1)
    }

    @Test
    fun testShouldSendMessageToPublicUrlInfoTopic() {
        val objectMapper = ObjectMapper()
        val key = objectMapper.writeValueAsString("123456")
        val value = objectMapper.writeValueAsString("http://www.google.com")
        val urlInfo = UrlInfo("123456", "http://www.google.com")
        kafkaUrlInfoProducer.sendMessage(urlInfo)
        val received = consumerRecords.poll(5, TimeUnit.SECONDS)
        Assertions.assertEquals(key, received.key().toString())
        Assertions.assertEquals(value, received.value())
    }

    @AfterAll
    fun close() {
        container.stop()
        container.destroy()
    }
}