package com.anish.logprocessor.model

data class RawLog(
    val service: String,
    val level: String,
    val message: String,
    val timestamp: String
)
