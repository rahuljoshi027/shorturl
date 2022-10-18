package com.shorturl.domain.url.service

import com.shorturl.domain.url.exception.BadLongUrlException
import com.shorturl.domain.url.exception.URLPingException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.UnknownHostException

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class PingUrlService {

    private val logger = LoggerFactory.getLogger(PingUrlService::class.java)

    fun ping(longUrl: String) {
        try {
            val url = URL(longUrl)
            val huc: HttpURLConnection = url.openConnection() as HttpURLConnection
            if (huc.responseCode < HttpStatus.OK.value() && huc.responseCode >= HttpStatus.MULTIPLE_CHOICES.value())
                throw BadLongUrlException(
                    "Invalid input url"
                )
        } catch (ex: Exception) {
            when (ex) {
                is MalformedURLException, is UnknownHostException -> {
                    logger.error(" input url : $longUrl , is invalid")
                    throw BadLongUrlException("Invalid input url")
                }

                else -> throw URLPingException("Cannot ping URL")
            }
        }
    }
}