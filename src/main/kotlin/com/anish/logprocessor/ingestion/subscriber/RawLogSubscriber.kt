package com.anish.logprocessor.ingestion.subscriber

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class RawLogSubscriber {
    companion object {
        private val log = LoggerFactory.getLogger(RawLogSubscriber::class.java)
    }

    fun processRawLog(): Consumer<String> {
        return Consumer{ rawLog ->
            log.info("Received raw log: {}", rawLog)

        }
    }
}