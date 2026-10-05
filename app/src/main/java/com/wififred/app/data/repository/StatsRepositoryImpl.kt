package com.wififred.app.data.repository

import com.wififred.app.domain.model.TrafficStats
import com.wififred.app.domain.repository.StatsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepositoryImpl @Inject constructor() : StatsRepository {

    private var totalDown = 0L
    private var totalUp = 0L

    override fun observeLiveStats(): Flow<TrafficStats> = flow {
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsed = (System.currentTimeMillis() - startTime) / 1000
            emit(
                TrafficStats(
                    pingMs = Random.nextLong(10L, 80L),
                    downloadKbps = Random.nextDouble(500.0, 5000.0),
                    uploadKbps = Random.nextDouble(200.0, 2000.0),
                    totalDownloadedBytes = totalDown,
                    totalUploadedBytes = totalUp,
                    sessionDurationSeconds = elapsed
                )
            )
            totalDown += Random.nextLong(100_000L, 2_000_000L)
            totalUp += Random.nextLong(50_000L, 500_000L)
            delay(1000)
        }
    }

    override suspend fun resetSession() {
        totalDown = 0L
        totalUp = 0L
    }

    override suspend fun getTotalBytes(): Pair<Long, Long> = totalDown to totalUp
}
