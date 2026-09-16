package com.anish.logprocessor.solace.publisher

import com.anish.logprocessor.model.LogEvent
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Component

@Component
class ProcessedLogPublisher (private val streamBridge: StreamBridge) {

    fun publish(logEvent: LogEvent) {

        val sent = streamBridge.send(
            "processedLogPublisher-out-0",
            logEvent
        )

        if(!sent) {
            throw IllegalStateException("Failed to publish processed log")
        }
    }
}
