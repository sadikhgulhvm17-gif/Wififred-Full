package com.wififred.app.data.local.dao

import com.wififred.app.data.local.entity.ServerEntity
import kotlinx.coroutines.flow.Flow

interface ServerDao {
    fun getAll(): Flow<List<ServerEntity>>
    suspend fun getById(id: Long): ServerEntity?
    suspend fun insert(server: ServerEntity): Long
    suspend fun update(server: ServerEntity)
    suspend fun delete(server: ServerEntity)
    suspend fun clearDefaults()
    suspend fun setDefault(id: Long)
}
