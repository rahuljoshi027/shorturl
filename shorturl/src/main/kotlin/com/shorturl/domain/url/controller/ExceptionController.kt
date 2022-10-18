package com.shorturl.domain.url.controller

import com.shorturl.domain.url.exception.BadLongUrlException
import com.shorturl.domain.url.exception.URLPingException
import com.shorturl.generated.api.DefaultExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class ExceptionController : DefaultExceptionHandler() {

    @ExceptionHandler(value = [BadLongUrlException::class])
    fun onBadLongUrlException(ex: BadLongUrlException, response: HttpServletResponse): Unit =
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.message)

    @ExceptionHandler(value = [URLPingException::class])
    fun onBadLongUrlException(ex: URLPingException, response: HttpServletResponse): Unit =
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.message)
}