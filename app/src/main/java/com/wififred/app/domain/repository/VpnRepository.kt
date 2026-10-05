package com.wififred.app.domain.repository

import com.wififred.app.domain.model.ConnectionState
import com.wififred.app.domain.model.ServerConfig
import com.wififred.app.domain.model.ServerLog
import kotlinx.coroutines.flow.Flow

interface VpnRepository {
    fun observeConnectionState(): Flow<ConnectionState>
    fun observeLogs(): Flow<List<ServerLog>>
    suspend fun connect(config: ServerConfig)
    suspend fun disconnect()
    suspend fun setSplitTunneling(packageNames: List<String>)
    suspend fun getSplitTunnelingApps(): List<String>
}
