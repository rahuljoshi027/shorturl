package com.redirect.zookeeper.kafka.service

import com.redirect.zookeeper.kafka.config.KafkaTopologyConstants
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.stereotype.Service

@Service
class StateStoreQueryService(val streamsBuilderFactoryBean: StreamsBuilderFactoryBean) {

    fun findValueforKey(key: String): String? {
        return readOnlyKeyValueStore()?.get(key)
    }

    private fun readOnlyKeyValueStore(): ReadOnlyKeyValueStore<String, String>? {
        val storeQueryParams =
            StoreQueryParameters.fromNameAndType<ReadOnlyKeyValueStore<String, String>>(
                KafkaTopologyConstants.URL_INFO_STORE,
                QueryableStoreTypes.keyValueStore()
            )
        return streamsBuilderFactoryBean.kafkaStreams?.store(storeQueryParams)
    }
}