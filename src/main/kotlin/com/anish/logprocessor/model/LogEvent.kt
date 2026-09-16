package com.anish.logprocessor.model

data class LogEvent(
    val eventId: String,
    val service: String,
    val level: String,
    val message: String,
    val originalTimestamp: String,
    val ingestionTimestamp: String,
    val rawLog: String
)
