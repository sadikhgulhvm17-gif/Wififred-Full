package com.wififred.app.presentation.screens.vpn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wififred.app.domain.model.ConnectionState
import com.wififred.app.domain.repository.VpnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class VpnViewModel @Inject constructor(
    private val vpnRepository: VpnRepository
) : ViewModel() {

    val connectionState: StateFlow<ConnectionState> =
        vpnRepository.observeConnectionState()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ConnectionState.Disconnected
            )
}
