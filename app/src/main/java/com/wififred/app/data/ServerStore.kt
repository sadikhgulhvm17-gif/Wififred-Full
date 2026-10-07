package com.wififred.app.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ServerModel(
    val id: Long,
    val name: String,
    val host: String,
    val port: Int,
    val protocol: String,
    val isDefault: Boolean = false,
    val username: String = "",
    val password: String = ""
)

object ServerStore {
    private var prefs: AppPreferences? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _servers = MutableStateFlow<List<ServerModel>>(emptyList())
    val servers: StateFlow<List<ServerModel>> = _servers

    fun init(context: Context) {
        if (prefs != null) return
        val p = AppPreferences(context.applicationContext)
        prefs = p
        scope.launch {
            p.serversJson.collect { json ->
                _servers.value = parse(json)
            }
        }
    }

    fun add(name: String, host: String, port: Int, protocol: String, username: String = "", password: String = "") {
        val newId = (_servers.value.maxOfOrNull { it.id } ?: 0L) + 1
        _servers.value = _servers.value + ServerModel(newId, name, host, port, protocol, false, username, password)
        save()
        LogStore.info("إضافة خادم: $name ($host:$port)")
    }

    fun update(server: ServerModel) {
        _servers.value = _servers.value.map { if (it.id == server.id) server else it }
        save()
        LogStore.info("تعديل خادم: ${server.name}")
    }

    fun remove(id: Long) {
        val removed = _servers.value.find { it.id == id }
        _servers.value = _servers.value.filter { it.id != id }
        save()
        if (removed != null) LogStore.warn("حذف خادم: ${removed.name}")
    }

    fun setDefault(id: Long) {
        _servers.value = _servers.value.map { it.copy(isDefault = it.id == id) }
        save()
        val name = _servers.value.find { it.id == id }?.name ?: ""
        LogStore.info("تعيين الخادم الافتراضي: $name")
    }

    fun getDefault(): ServerModel? = _servers.value.find { it.isDefault }

    private fun save() {
        val json = _servers.value.joinToString("\n") {
            listOf(
                it.id.toString(), it.name, it.host, it.port.toString(),
                it.protocol, it.isDefault.toString(), it.username, it.password
            ).joinToString("|||")
        }
        scope.launch { prefs?.setServersJson(json) }
    }

    private fun parse(json: String): List<ServerModel> {
        if (json.isBlank()) return emptyList()
        return json.split("\n").mapNotNull { line ->
            val p = line.split("|||")
            if (p.size < 6) null
            else ServerModel(
                id = p[0].toLongOrNull() ?: 0L,
                name = p[1],
                host = p[2],
                port = p[3].toIntOrNull() ?: 22,
                protocol = p[4],
                isDefault = p[5].toBooleanStrictOrNull() ?: false,
                username = if (p.size > 6) p[6] else "",
                password = if (p.size > 7) p[7] else ""
            )
        }
    }
}
