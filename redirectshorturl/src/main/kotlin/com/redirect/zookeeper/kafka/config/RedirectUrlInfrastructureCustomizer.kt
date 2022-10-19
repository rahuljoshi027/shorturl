package com.redirect.zookeeper.kafka.config

import com.redirect.zookeeper.kafka.processor.RedirectUrlInfoStoreProcessor
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import org.apache.kafka.streams.state.Stores
import org.springframework.kafka.config.KafkaStreamsInfrastructureCustomizer
import org.springframework.kafka.support.serializer.JsonDeserializer

class RedirectUrlInfrastructureCustomizer : KafkaStreamsInfrastructureCustomizer {

    override fun configureBuilder(builder: StreamsBuilder) {
        val stateStoreBuilder =
            Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(KafkaTopologyConstants.URL_INFO_STORE),
                Serdes.String(),
                Serdes.String()
            ).withLoggingDisabled()


        val keyDe: Deserializer<String> = JsonDeserializer(
            String::class.java
        )
        val valueDe: Deserializer<String> = JsonDeserializer(
            String::class.java
        )

        val topology: Topology = builder.build()
        topology.addGlobalStore(stateStoreBuilder,
            KafkaTopicConfig.publicUrlInfoTopic,
            keyDe,
            valueDe,
            KafkaTopicConfig.publicUrlInfoTopic,
            KafkaTopologyConstants.URL_REDIRECTION_PROCESSOR,
            ProcessorSupplier { RedirectUrlInfoStoreProcessor() })

    }

}