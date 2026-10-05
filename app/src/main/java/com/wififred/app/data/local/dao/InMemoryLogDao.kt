package com.wififred.app.data.local.dao

import com.wififred.app.data.local.entity.LogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class InMemoryLogDao : LogDao {
    private val storage = MutableStateFlow<List<LogEntity>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<LogEntity>> = storage

    override suspend fun insert(log: LogEntity) {
        val newLog = log.copy(id = if (log.id == 0L) nextId++ else log.id)
        storage.value = (listOf(newLog) + storage.value).take(500)
    }

    override suspend fun clearAll() {
        storage.value = emptyList()
    }
}
