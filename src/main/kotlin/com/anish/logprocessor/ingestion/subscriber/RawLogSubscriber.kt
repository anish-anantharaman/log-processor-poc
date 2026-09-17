package com.anish.logprocessor.ingestion.subscriber

import com.anish.logprocessor.ingestion.dmq.DmqPublisher
import com.anish.logprocessor.ingestion.mapper.LogEventMapper
import com.anish.logprocessor.ingestion.parser.RawLogParser
import com.anish.logprocessor.model.DmqMessage
import com.anish.logprocessor.solace.publisher.ProcessedLogPublisher
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.function.Consumer

@Component
class RawLogSubscriber(
    private val rawLogParser: RawLogParser,
    private val logEventMapper: LogEventMapper,
    private val processedLogPublisher: ProcessedLogPublisher,
    private val dmqPublisher: DmqPublisher
    ) {
    companion object {
        private val log = LoggerFactory.getLogger(RawLogSubscriber::class.java)
    }

    @Bean
    fun rawLogSubscriber(): Consumer<String> {
        return Consumer{ payload ->
            log.info("Received raw log: {}", payload)
                process(payload)
        }
    }

    private fun process(payload: String) {
        try {
            // 1. Parse and validate
            val rawLog = rawLogParser.parseAndValidate(payload)

            // 2. Build canonical LogEvent
            val logEvent = logEventMapper.toLogEvent(rawLog, payload);

            // 3. Publish successful message
            processedLogPublisher.publish(logEvent);
        } catch (e: Exception) {

            // 4. Publish invalid message to DMQ
            val dmqMessage = DmqMessage(
                payload,
                e.message ?: "Unknown processing error",
                "PARSE_AND_VALIDATE",
                Instant.now().toString()
            )
            dmqPublisher.publish(dmqMessage)
        }
    }
}