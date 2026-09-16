package com.anish.logprocessor.ingestion.dmq

import com.anish.logprocessor.model.DmqMessage
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Component

@Component
class DmqPublisher(private val streamBridge: StreamBridge) {

    fun publish(dmqMessage: DmqMessage) {
        val sent = streamBridge.send("dmqPublisher-out-0",
            dmqMessage
        )

        if(!sent) {
            throw IllegalStateException("Failed to publish message to DMQ")
        }
    }
}