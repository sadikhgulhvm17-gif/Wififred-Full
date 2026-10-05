package com.wififred.app.data.local.entity

data class LogEntity(
    val id: Long = 0,
    val timestamp: Long,
    val level: String,
    val message: String
)
