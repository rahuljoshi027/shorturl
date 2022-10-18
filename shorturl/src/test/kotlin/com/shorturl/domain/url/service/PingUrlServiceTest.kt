package com.shorturl.domain.url.service

import com.shorturl.domain.url.exception.BadLongUrlException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PingUrlServiceTest {

    @Test
    fun shouldThrowExceptionOnUnKnowHostException() {
        val pingUrlService = PingUrlService()

        assertThrows<BadLongUrlException> {
            pingUrlService.ping("http://www.google")
        }
    }

    @Test
    fun shouldThrowExceptionOnMalformedURLException() {
        val pingUrlService = PingUrlService()

        assertThrows<BadLongUrlException> {
            pingUrlService.ping("www.google.com")
        }
    }
}