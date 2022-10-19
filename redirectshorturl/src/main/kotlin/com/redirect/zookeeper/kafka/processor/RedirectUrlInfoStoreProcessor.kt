package com.redirect.zookeeper.kafka.processor

import com.redirect.zookeeper.kafka.config.KafkaTopologyConstants
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.Record
import org.apache.kafka.streams.state.KeyValueStore
import org.springframework.stereotype.Service

@Service
class RedirectUrlInfoStoreProcessor :
    org.apache.kafka.streams.processor.api.Processor<String, String, Void, Void> {

    lateinit var stateStore: KeyValueStore<String, String>

    override fun init(context: ProcessorContext<Void, Void>?) {
        super.init(context)
        if (context != null) {
            stateStore = context.getStateStore(KafkaTopologyConstants.URL_INFO_STORE)
                    as KeyValueStore<String, String>
        }
    }

    override fun process(record: Record<String, String>?) {
        stateStore.put(record?.key(), record?.value())
    }


}