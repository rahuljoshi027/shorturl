package com.redirect.domain.url.service

import com.redirect.domain.url.exception.KeyNotFoundException
import com.redirect.zookeeper.kafka.service.StateStoreQueryService
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class FetchLongUrlService(private val stateStoreQueryService: StateStoreQueryService) {

    fun fetchLongUrl(key: String): String? {
        return stateStoreQueryService.findValueforKey(key) ?: throw KeyNotFoundException("Invalid key")
    }
}