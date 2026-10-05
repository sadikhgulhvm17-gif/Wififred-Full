package com.wififred.app.domain.model

/**
 * حالة الاتصال بالنفق
 */
sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    data class Connected(val server: ServerConfig) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
    object Reconnecting : ConnectionState()
}
