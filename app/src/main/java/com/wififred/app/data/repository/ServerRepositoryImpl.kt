package com.wififred.app.data.repository

import com.google.gson.Gson
import com.wififred.app.data.local.dao.ServerDao
import com.wififred.app.data.mapper.toDomain
import com.wififred.app.data.mapper.toEntity
import com.wififred.app.data.remote.dto.ServerDto
import com.wififred.app.domain.model.ServerConfig
import com.wififred.app.domain.repository.ServerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRepositoryImpl @Inject constructor(
    private val dao: ServerDao,
    private val gson: Gson
) : ServerRepository {

    override fun getAllServers(): Flow<List<ServerConfig>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getServerById(id: Long): ServerConfig? =
        dao.getById(id)?.toDomain()

    override suspend fun addServer(config: ServerConfig): Long =
        dao.insert(config.toEntity())

    override suspend fun updateServer(config: ServerConfig) =
        dao.update(config.toEntity())

    override suspend fun deleteServer(id: Long) {
        dao.getById(id)?.let { dao.delete(it) }
    }

    override suspend fun importConfig(json: String): ServerConfig {
        val dto = gson.fromJson(json, ServerDto::class.java)
        val config = dto.toDomain()
        val newId = dao.insert(config.toEntity())
        return config.copy(id = newId)
    }

    override suspend fun exportConfig(id: Long): String {
        val server = dao.getById(id) ?: error("Server not found")
        val dto = ServerDto(
            name = server.name,
            host = server.host,
            port = server.port,
            protocol = server.protocol,
            username = server.username,
            password = server.password,
            privateKey = server.privateKey
        )
        return gson.toJson(dto)
    }

    override suspend fun setDefaultServer(id: Long) {
        dao.clearDefaults()
        dao.setDefault(id)
    }
}
