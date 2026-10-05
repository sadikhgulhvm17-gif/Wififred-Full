package com.wififred.app.data.local.dao

import com.wififred.app.data.local.entity.ServerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class InMemoryServerDao : ServerDao {
    private val storage = MutableStateFlow<List<ServerEntity>>(emptyList())
    private var nextId = 1L

    override fun getAll(): Flow<List<ServerEntity>> = storage

    override suspend fun getById(id: Long): ServerEntity? =
        storage.value.firstOrNull { it.id == id }

    override suspend fun insert(server: ServerEntity): Long {
        val id = if (server.id == 0L) nextId++ else server.id
        val newServer = server.copy(id = id)
        storage.value = storage.value.filter { it.id != id } + newServer
        return id
    }

    override suspend fun update(server: ServerEntity) {
        storage.value = storage.value.map { if (it.id == server.id) server else it }
    }

    override suspend fun delete(server: ServerEntity) {
        storage.value = storage.value.filter { it.id != server.id }
    }

    override suspend fun clearDefaults() {
        storage.value = storage.value.map { it.copy(isDefault = false) }
    }

    override suspend fun setDefault(id: Long) {
        storage.value = storage.value.map { it.copy(isDefault = it.id == id) }
    }
}
