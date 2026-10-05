package com.wififred.app.domain.model

/**
 * إعدادات الخادم البعيد (VPS)
 */
data class ServerConfig(
    val id: Long = 0L,
    val name: String,
    val host: String,
    val port: Int,
    val protocol: VpnProtocol,
    val username: String = "",
    val password: String = "",
    val privateKey: String = "",
    val isDefault: Boolean = false
)

enum class VpnProtocol {
    SSH,
    SOCKS5,
    V2RAY_VMESS,
    V2RAY_VLESS,
    DNS_TUNNEL,
    WIREGUARD
}
