package com.wififred.app.domain.model

data class ServerLog(
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel,
    val message: String
)

enum class LogLevel { INFO, WARNING, ERROR, DEBUG }
