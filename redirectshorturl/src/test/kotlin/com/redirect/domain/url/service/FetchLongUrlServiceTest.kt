package com.redirect.domain.url.service

import com.redirect.domain.url.exception.KeyNotFoundException
import com.redirect.zookeeper.kafka.service.StateStoreQueryService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FetchLongUrlServiceTest {

    @BeforeAll
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun shouldReturnLongUrlForValidKey() {
        val expected = "http://www.google.com"
        val stateStoreQueryService = mock(StateStoreQueryService::class.java)
        Mockito.`when`(stateStoreQueryService.findValueforKey("123456"))
            .thenReturn(expected)
        val fetchLongUrlService = FetchLongUrlService(stateStoreQueryService)
        val actual = fetchLongUrlService.fetchLongUrl("123456")
        assertEquals(expected, actual)
    }

    @Test
    fun shouldThrowKeyNotFoundExceptionForInValidKey() {
        val stateStoreQueryService = mock(StateStoreQueryService::class.java)
        val fetchLongUrlService = FetchLongUrlService(stateStoreQueryService)
        assertThrows<KeyNotFoundException> {
            fetchLongUrlService.fetchLongUrl("123456")
        }
    }
}