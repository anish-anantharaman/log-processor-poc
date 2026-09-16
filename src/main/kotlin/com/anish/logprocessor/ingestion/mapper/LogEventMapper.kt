package com.anish.logprocessor.ingestion.mapper

import com.anish.logprocessor.model.LogEvent
import com.anish.logprocessor.model.RawLog
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class LogEventMapper {

    fun toLogEvent(rawLog: RawLog, originalPayload: String): LogEvent {
        return LogEvent(
            UUID.randomUUID().toString(),
            rawLog.service,
            rawLog.level,
            rawLog.message,
            rawLog.timestamp,
            Instant.now().toString(),
            originalPayload
        )
    }
}