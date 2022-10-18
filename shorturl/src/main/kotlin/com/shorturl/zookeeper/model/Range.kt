package com.shorturl.zookeeper.model

import java.util.concurrent.atomic.AtomicLong

data class Range(
    var start: Long,
    var end: Long,
    var next: AtomicLong
) {
}