package com.redirect.zookeeper.kafka.processor

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.processor.api.MockProcessorContext
import org.apache.kafka.streams.processor.api.Record
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.Stores
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedirectUrlInfoStoreProcessorTest {

    private lateinit var context: MockProcessorContext<Void, Void>
    private val redirectUrlInfoStoreProcessor = RedirectUrlInfoStoreProcessor()

    @BeforeAll
    fun setup() {
        MockitoAnnotations.openMocks(this)
        context = MockProcessorContext<Void, Void>()
        val store = Stores.keyValueStoreBuilder(
            Stores.inMemoryKeyValueStore("urlInfoStore"),
            Serdes.String(),
            Serdes.String()
        ).withLoggingDisabled().build()
        store.init(context.stateStoreContext, store)
        context.addStateStore<KeyValueStore<String, String>>(store)
        redirectUrlInfoStoreProcessor.init(context)
    }

    @Test
    fun shouldAddEntriesToSateStore() {
        val record = Record("12345", "http://www.google.com", 1000L)
        redirectUrlInfoStoreProcessor.process(record)
        val store = context.getStateStore<KeyValueStore<String, String>>("urlInfoStore")
        assertEquals(1, store.approximateNumEntries())
    }
}