package com.anish.logprocessor.model

import jakarta.validation.Payload

data class DmqMessage(
    val originalPayload: String,
    val failureReason: String,
    val failedAtStage: String,
    val failureTimestamp: String
)
