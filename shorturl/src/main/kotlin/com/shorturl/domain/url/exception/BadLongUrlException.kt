package com.shorturl.domain.url.exception

class BadLongUrlException : RuntimeException {
    constructor(message: String) : super(message)
}