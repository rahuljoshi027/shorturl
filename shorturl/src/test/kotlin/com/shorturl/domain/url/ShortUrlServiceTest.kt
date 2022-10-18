package com.shorturl.domain.url

import com.shorturl.domain.url.exception.BadLongUrlException
import com.shorturl.domain.url.service.ShortUrlService
import com.shorturl.generated.model.MakeShortUrlResponse
import com.shorturl.zookeeper.kafka.model.UrlInfo
import com.shorturl.zookeeper.kafka.producer.KafkaUrlInfoProducer
import com.shorturl.zookeeper.service.SharedRangeService
import org.hashids.Hashids
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.net.InetAddress

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShortUrlServiceTest {

    @BeforeAll
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun shouldReturnValidOutputOnValidInput() {
        val salt = "salt"
        val redirectionServicePort = "8080"
        val ip = InetAddress.getLocalHost().hostAddress
        val hashids = Hashids(salt, 8)
        val hash = hashids.encode(99)
        val urlInfo = UrlInfo(hash, "http://www.google.com")
        val shotUrl = "http://$ip:$redirectionServicePort/v1/$hash"
        val sharedRangeService = mock(SharedRangeService::class.java)
        val kafkaUrlInfoProducer = mock(KafkaUrlInfoProducer::class.java)
        val shortUrlService = ShortUrlService(sharedRangeService, kafkaUrlInfoProducer, salt, redirectionServicePort)

        Mockito.`when`(sharedRangeService.nextCounter()).thenReturn(99)
        Mockito.doNothing().`when`(kafkaUrlInfoProducer).sendMessage(urlInfo)

        val actual = shortUrlService.createAndPublishShortUrl("http://www.google.com")
        assertEquals(MakeShortUrlResponse(shotUrl), actual)
    }

    @Test
    fun shouldThrowExceptionOnUnKnowHostException() {
        val salt = "salt"
        val redirectionServicePort = "8080"

        val sharedRangeService = mock(SharedRangeService::class.java)
        val kafkaUrlInfoProducer = mock(KafkaUrlInfoProducer::class.java)
        val shortUrlService = ShortUrlService(sharedRangeService, kafkaUrlInfoProducer, salt, redirectionServicePort)

        assertThrows<BadLongUrlException> {
            shortUrlService.createAndPublishShortUrl("http://www.google")
        }
    }

    @Test
    fun shouldThrowExceptionOnMalformedURLException() {
        val salt = "salt"
        val redirectionServicePort = "8080"

        val sharedRangeService = mock(SharedRangeService::class.java)
        val kafkaUrlInfoProducer = mock(KafkaUrlInfoProducer::class.java)
        val shortUrlService = ShortUrlService(sharedRangeService, kafkaUrlInfoProducer, salt, redirectionServicePort)

        assertThrows<BadLongUrlException> {
            shortUrlService.createAndPublishShortUrl("www.google.com")
        }
    }
}