package com.wififred.app.domain.repository

import com.wififred.app.domain.model.TrafficStats
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun observeLiveStats(): Flow<TrafficStats>
    suspend fun resetSession()
    suspend fun getTotalBytes(): Pair<Long, Long>
}
