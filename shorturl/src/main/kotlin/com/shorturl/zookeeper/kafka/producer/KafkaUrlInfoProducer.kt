package com.shorturl.zookeeper.kafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.shorturl.zookeeper.kafka.config.KafkaTopicConfig.Companion.publicUrlInfoTopic
import com.shorturl.zookeeper.kafka.model.UrlInfo
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaUrlInfoProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {

    val objectMapper = ObjectMapper()

    fun sendMessage(urlInfo: UrlInfo) {
        kafkaTemplate.send(
            publicUrlInfoTopic,
            objectMapper.writeValueAsString(urlInfo.hash),
            objectMapper.writeValueAsString(urlInfo.longUrl)
        )
    }
}