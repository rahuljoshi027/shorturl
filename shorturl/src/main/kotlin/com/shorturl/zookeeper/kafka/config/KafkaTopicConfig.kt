package com.shorturl.zookeeper.kafka.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaTopicConfig(@Value("\${kafka.bootstrap.address}") val bootstrapAddress: String) {

    companion object {
        const val publicUrlInfoTopic = "public.url-info-topic.v1"
    }

    @Bean
    fun kafkaAdmin(): KafkaAdmin? {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        return KafkaAdmin(configs)
    }

    @Bean
    fun topicPrivateCustomerDataInput(): NewTopic? {
        return NewTopic(
            publicUrlInfoTopic,
            1, 1
        )
    }
}