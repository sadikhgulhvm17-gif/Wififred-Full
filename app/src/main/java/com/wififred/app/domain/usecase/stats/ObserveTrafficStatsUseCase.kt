package com.wififred.app.domain.usecase.stats

import com.wififred.app.domain.model.TrafficStats
import com.wififred.app.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTrafficStatsUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    operator fun invoke(): Flow<TrafficStats> = repository.observeLiveStats()
}
