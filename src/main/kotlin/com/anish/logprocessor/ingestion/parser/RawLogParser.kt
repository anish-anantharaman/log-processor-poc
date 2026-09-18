package com.anish.logprocessor.ingestion.parser

import com.anish.logprocessor.model.RawLog
import com.anish.logprocessor.util.enums.LogLevel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue
import java.time.Instant
import java.time.format.DateTimeParseException

@Component
class RawLogParser(private val objectMapper: ObjectMapper) {
    companion object {
        private val log = LoggerFactory.getLogger(RawLogParser::class.java)
    }

    fun parseAndValidate(payload: String): RawLog {
        val rawLog = try {
            objectMapper.readValue<RawLog>(payload)
        } catch (e: Exception) {
            val reason = e.message?.takeIf { it.isNotBlank() } ?: "Malformed JSON payload"
            log.error("Invalid JSON payload: {}", reason, e)
            throw IllegalArgumentException("Invalid JSON payload: $reason", e)
        }
        validate(rawLog)
        return rawLog
    }

    private fun validate(rawLog: RawLog) {

        val errors = mutableListOf<String>()

        if (rawLog.service.isBlank()) {
            errors.add("'service' is missing or empty")
        }

        if (rawLog.level.isBlank()) {
            errors.add("'level' is missing or empty")
        } else if (LogLevel.entries.none { it.name.equals(rawLog.level, ignoreCase = false) }) {
            errors.add(
                "Invalid 'level' value: ${rawLog.level}. " +
                        "Allowed values are: ${LogLevel.entries.joinToString()}"
            )
        }

        if (rawLog.message.isBlank()) {
            errors.add("'message' is missing or empty")
        }

        if (rawLog.timestamp.isBlank()) {
            errors.add("'timestamp' is missing or empty")
        } else {
            validateTimestamp(rawLog.timestamp, errors)
        }

        if (errors.isNotEmpty()) {
            throw IllegalArgumentException(
                errors.joinToString(". ")
            )
        }
    }

    private fun validateTimestamp(timestamp: String, errors: MutableList<String>) {
        try {
            Instant.parse(timestamp)
        } catch (e: DateTimeParseException) {
            log.error("Invalid 'timestamp': {}", e.message, e)
            errors.add("Invalid 'timestamp' value: $timestamp")
        }
    }
}