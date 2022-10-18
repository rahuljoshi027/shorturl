package com.shorturl.domain.url.controller

import com.shorturl.domain.url.service.ShortUrlService
import com.shorturl.generated.api.V1Api
import com.shorturl.generated.model.MakeShortUrlRequest
import com.shorturl.generated.model.MakeShortUrlResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.annotation.RequestScope
import javax.validation.Valid

@RestController
@RequestScope
class ShortUrlController(
    val shortUrlService: ShortUrlService,
) : V1Api {

    override fun makeUrlShort(@Valid @RequestBody(required = true) makeShortUrlRequest: MakeShortUrlRequest?):
            ResponseEntity<MakeShortUrlResponse> {
        val makeShortUrlResponse = makeShortUrlRequest?.let {
            shortUrlService.createAndPublishShortUrl(it.url)
        }
        return ResponseEntity(makeShortUrlResponse, HttpStatus.CREATED)
    }
}
