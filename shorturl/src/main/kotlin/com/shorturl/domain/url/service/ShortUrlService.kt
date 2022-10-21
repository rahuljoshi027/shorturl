package com.shorturl.domain.url.service

import com.shorturl.domain.url.model.ShortUrl
import com.shorturl.generated.model.MakeShortUrlResponse
import com.shorturl.zookeeper.kafka.model.UrlInfo
import com.shorturl.zookeeper.kafka.producer.KafkaUrlInfoProducer
import com.shorturl.zookeeper.service.SharedRangeService
import org.hashids.Hashids
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.net.InetAddress

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class ShortUrlService(
    private val sharedRangeService: SharedRangeService,
    private val kafkaUrlInfoProducer: KafkaUrlInfoProducer,
    @Value("\${hashids.start.salt}") val hashSalt: String,
    @Value("\${redirection.service.port}") val redirectionServicePort: String,
    @Value("\${redirection.service.host}") val redirectionServiceHost: String
) {
    private val hashids = Hashids(hashSalt, 8)
    private val ip = InetAddress.getLocalHost().hostAddress
    private val logger = LoggerFactory.getLogger(ShortUrlService::class.java)

    fun createAndPublishShortUrl(longUrl: String): MakeShortUrlResponse {
        val shortUrl = createShortUrl()
        publish(shortUrl, longUrl)
        logger.info("created short url successfully")
        return MakeShortUrlResponse(shortUrl.url)
    }

    private fun publish(shortUrl: ShortUrl, longUrl: String) {
        kafkaUrlInfoProducer.sendMessage(UrlInfo(shortUrl.hash, longUrl))
        logger.info("published url info successfully")
    }

    private fun createShortUrl(): ShortUrl {
        val hash = hashids.encode(sharedRangeService.nextCounter())
        return ShortUrl("http://$redirectionServiceHost:$redirectionServicePort/v1/$hash", hash)
    }
}