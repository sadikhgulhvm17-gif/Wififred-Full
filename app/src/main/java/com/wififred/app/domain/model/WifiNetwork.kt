package com.wififred.app.domain.model

/**
 * نموذج يمثل شبكة WiFi مكتشفة
 */
data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val signalStrength: Int,       // 0..100
    val frequency: Int,            // MHz
    val securityType: SecurityType,
    val isConnected: Boolean = false
) {
    /** تقييم أمان الشبكة من 0 إلى 5 */
    val securityScore: Int
        get() = when (securityType) {
            SecurityType.WPA3 -> 5
            SecurityType.WPA2 -> 4
            SecurityType.WPA -> 3
            SecurityType.WEP -> 1
            SecurityType.OPEN -> 0
        }
}

enum class SecurityType {
    OPEN, WEP, WPA, WPA2, WPA3
}
