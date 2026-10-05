package com.wififred.app.domain.repository

import com.wififred.app.domain.model.ServerConfig
import kotlinx.coroutines.flow.Flow

interface ServerRepository {
    fun getAllServers(): Flow<List<ServerConfig>>
    suspend fun getServerById(id: Long): ServerConfig?
    suspend fun addServer(config: ServerConfig): Long
    suspend fun updateServer(config: ServerConfig)
    suspend fun deleteServer(id: Long)
    suspend fun importConfig(json: String): ServerConfig
    suspend fun exportConfig(id: Long): String
    suspend fun setDefaultServer(id: Long)
}
