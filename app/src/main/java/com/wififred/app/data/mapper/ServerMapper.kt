package com.wififred.app.data.mapper

import com.wififred.app.data.local.entity.ServerEntity
import com.wififred.app.data.remote.dto.ServerDto
import com.wififred.app.domain.model.ServerConfig
import com.wififred.app.domain.model.VpnProtocol

// Entity -> Domain
fun ServerEntity.toDomain(): ServerConfig = ServerConfig(
    id = id,
    name = name,
    host = host,
    port = port,
    protocol = runCatching { VpnProtocol.valueOf(protocol) }.getOrDefault(VpnProtocol.SSH),
    username = username,
    password = password,
    privateKey = privateKey,
    isDefault = isDefault
)

// Domain -> Entity
fun ServerConfig.toEntity(): ServerEntity = ServerEntity(
    id = id,
    name = name,
    host = host,
    port = port,
    protocol = protocol.name,
    username = username,
    password = password,
    privateKey = privateKey,
    isDefault = isDefault
)

// DTO -> Domain
fun ServerDto.toDomain(): ServerConfig = ServerConfig(
    name = name.ifBlank { "Imported Server" },
    host = host,
    port = if (port > 0) port else 22,
    protocol = runCatching { VpnProtocol.valueOf(protocol.uppercase()) }.getOrDefault(VpnProtocol.SSH),
    username = username,
    password = password,
    privateKey = privateKey
)
