package com.wififred.app.presentation.screens.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wififred.app.domain.model.ServerLog
import com.wififred.app.domain.repository.VpnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val vpnRepository: VpnRepository
) : ViewModel() {

    val logs: StateFlow<List<ServerLog>> =
        vpnRepository.observeLogs().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
