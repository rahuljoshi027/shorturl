package com.redirect.zookeeper.kafka.config

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.config.StreamsBuilderFactoryBean

@Configuration
@EnableKafka
class kafaStreamsConfig(@Value("\${kafka.bootstrap.address}") val bootstrapAddress: String) {

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun kStreamsConfig(): KafkaStreamsConfiguration? {
        val props: MutableMap<String, Any> = HashMap()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "streams-app"
        props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        return KafkaStreamsConfiguration(props)
    }

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME])
    @Primary
    @Throws(
        Exception::class
    )
    fun streamsBuilderFactoryBean(kafkaStreamsConfigConfiguration: KafkaStreamsConfiguration?): StreamsBuilderFactoryBean? {
        val streamsBuilderFactoryBean = StreamsBuilderFactoryBean(
            kafkaStreamsConfigConfiguration!!
        )
        streamsBuilderFactoryBean.afterPropertiesSet()
        streamsBuilderFactoryBean.setInfrastructureCustomizer(RedirectUrlInfrastructureCustomizer())
        streamsBuilderFactoryBean.setCloseTimeout(10) //10 seconds
        return streamsBuilderFactoryBean
    }

}