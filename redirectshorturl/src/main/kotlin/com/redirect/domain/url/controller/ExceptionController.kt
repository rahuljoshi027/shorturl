package com.redirect.domain.url.controller

import com.redirect.domain.url.exception.KeyNotFoundException
import com.redirect.generated.api.DefaultExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class ExceptionController : DefaultExceptionHandler() {

    @ExceptionHandler(value = [KeyNotFoundException::class])
    fun onKeyNotFoundException(ex: KeyNotFoundException, response: HttpServletResponse): Unit =
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.message)
}