package com.wififred.app.data.mapper

import com.wififred.app.data.local.entity.LogEntity
import com.wififred.app.domain.model.LogLevel
import com.wififred.app.domain.model.ServerLog

fun LogEntity.toDomain(): ServerLog = ServerLog(
    timestamp = timestamp,
    level = runCatching { LogLevel.valueOf(level) }.getOrDefault(LogLevel.INFO),
    message = message
)

fun ServerLog.toEntity(): LogEntity = LogEntity(
    timestamp = timestamp,
    level = level.name,
    message = message
)
