package com.wififred.app.data.repository

import com.wififred.app.data.local.dao.LogDao
import com.wififred.app.data.local.entity.LogEntity
import com.wififred.app.data.mapper.toDomain
import com.wififred.app.domain.model.ConnectionState
import com.wififred.app.domain.model.ServerConfig
import com.wififred.app.domain.model.ServerLog
import com.wififred.app.domain.repository.VpnRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VpnRepositoryImpl @Inject constructor(
    private val logDao: LogDao
) : VpnRepository {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    private val _splitApps = mutableSetOf<String>()

    override fun observeConnectionState(): Flow<ConnectionState> = _connectionState

    override fun observeLogs(): Flow<List<ServerLog>> =
        logDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun connect(config: ServerConfig) {
        _connectionState.value = ConnectionState.Connecting
        log("بدء الاتصال بالخادم: ${config.name} (${config.host}:${config.port})")

        // محاكاة الاتصال - سيتم استبداله بـ VpnService الحقيقي لاحقاً
        delay(1500)
        log("تم التحقق من الهوية بنجاح")
        log("تم إنشاء النفق الآمن")

        _connectionState.value = ConnectionState.Connected(config)
    }

    override suspend fun disconnect() {
        log("قطع الاتصال بالخادم")
        _connectionState.value = ConnectionState.Disconnected
    }

    override suspend fun setSplitTunneling(packageNames: List<String>) {
        _splitApps.clear()
        _splitApps.addAll(packageNames)
        log("تم تحديث قائمة Split Tunneling (${_splitApps.size} تطبيق)")
    }

    override suspend fun getSplitTunnelingApps(): List<String> = _splitApps.toList()

    private suspend fun log(message: String, level: String = "INFO") {
        logDao.insert(
            LogEntity(
                timestamp = System.currentTimeMillis(),
                level = level,
                message = message
            )
        )
    }
}
