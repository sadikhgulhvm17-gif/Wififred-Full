package com.wififred.app.domain.model

/**
 * إحصائيات حركة المرور الحية
 */
data class TrafficStats(
    val pingMs: Long = 0L,
    val downloadKbps: Double = 0.0,
    val uploadKbps: Double = 0.0,
    val totalDownloadedBytes: Long = 0L,
    val totalUploadedBytes: Long = 0L,
    val sessionDurationSeconds: Long = 0L
)
