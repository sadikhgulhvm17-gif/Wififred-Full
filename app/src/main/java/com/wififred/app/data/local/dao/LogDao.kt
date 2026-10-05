package com.wififred.app.data.local.dao

import com.wififred.app.data.local.entity.LogEntity
import kotlinx.coroutines.flow.Flow

interface LogDao {
    fun observeAll(): Flow<List<LogEntity>>
    suspend fun insert(log: LogEntity)
    suspend fun clearAll()
}
