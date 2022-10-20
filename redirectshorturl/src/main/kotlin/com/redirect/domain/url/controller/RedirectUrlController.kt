package com.redirect.domain.url.controller

import com.redirect.domain.url.service.FetchLongUrlService
import com.redirect.generated.api.V1Api
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.annotation.RequestScope
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@RestController
@RequestScope
class RedirectUrlController(private val fetchLongUrlService: FetchLongUrlService) : V1Api {

    override fun redirectToLongUrl(@PathVariable("key") key: String): ResponseEntity<Unit> {
        val response = (RequestContextHolder.currentRequestAttributes()
                as ServletRequestAttributes).response
        val value = fetchLongUrlService.fetchLongUrl(key)
        response?.sendRedirect(value)
        return ResponseEntity.ok().build()
    }
}