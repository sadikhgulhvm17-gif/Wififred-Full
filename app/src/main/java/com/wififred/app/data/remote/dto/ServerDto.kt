package com.wififred.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * نموذج استيراد/تصدير إعدادات الخادم
 * يدعم صيغ V2ray / SOCKS5 / SSH
 */
data class ServerDto(
    @SerializedName("name") val name: String = "",
    @SerializedName("host") val host: String = "",
    @SerializedName("port") val port: Int = 0,
    @SerializedName("protocol") val protocol: String = "SSH",
    @SerializedName("username") val username: String = "",
    @SerializedName("password") val password: String = "",
    @SerializedName("private_key") val privateKey: String = ""
)
