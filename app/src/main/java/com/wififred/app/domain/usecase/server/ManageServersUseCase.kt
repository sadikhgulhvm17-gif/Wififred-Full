package com.wififred.app.domain.usecase.server

import com.wififred.app.domain.model.ServerConfig
import com.wififred.app.domain.repository.ServerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageServersUseCase @Inject constructor(
    private val repository: ServerRepository
) {
    fun getAllServers(): Flow<List<ServerConfig>> = repository.getAllServers()
    suspend fun add(config: ServerConfig) = repository.addServer(config)
    suspend fun update(config: ServerConfig) = repository.updateServer(config)
    suspend fun delete(id: Long) = repository.deleteServer(id)
    suspend fun setDefault(id: Long) = repository.setDefaultServer(id)
    suspend fun import(json: String) = repository.importConfig(json)
    suspend fun export(id: Long) = repository.exportConfig(id)
}
