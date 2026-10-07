package com.wififred.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LogEntry(val timestamp: Long, val level: String, val message: String) {
    fun formatted(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}

object LogStore {
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs

    fun info(msg: String) { add("INFO", msg) }
    fun warn(msg: String) { add("WARN", msg) }
    fun error(msg: String) { add("ERROR", msg) }
    fun debug(msg: String) { add("DEBUG", msg) }

    private fun add(level: String, msg: String) {
        val entry = LogEntry(System.currentTimeMillis(), level, msg)
        _logs.value = (listOf(entry) + _logs.value).take(500)
    }

    fun clear() { _logs.value = emptyList() }
}
