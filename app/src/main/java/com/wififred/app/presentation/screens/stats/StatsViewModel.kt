package com.wififred.app.presentation.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wififred.app.domain.model.TrafficStats
import com.wififred.app.domain.usecase.stats.ObserveTrafficStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    observeTrafficStats: ObserveTrafficStatsUseCase
) : ViewModel() {

    val uiState: StateFlow<TrafficStats> = observeTrafficStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TrafficStats()
        )
}
