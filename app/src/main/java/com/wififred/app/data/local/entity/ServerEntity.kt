package com.wififred.app.data.local.entity

data class ServerEntity(
    val id: Long = 0,
    val name: String,
    val host: String,
    val port: Int,
    val protocol: String,
    val username: String,
    val password: String,
    val privateKey: String,
    val isDefault: Boolean
)
