package com.wififred.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wififred.app.domain.model.ConnectionState
import com.wififred.app.domain.repository.VpnRepository
import com.wififred.app.domain.usecase.vpn.ConnectToServerUseCase
import com.wififred.app.domain.usecase.vpn.DisconnectVpnUseCase
import com.wififred.app.domain.usecase.server.ManageServersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val activeServerName: String = "غير متصل",
    val hasServers: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vpnRepository: VpnRepository,
    private val manageServers: ManageServersUseCase,
    private val connectToServer: ConnectToServerUseCase,
    private val disconnectVpn: DisconnectVpnUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        vpnRepository.observeConnectionState(),
        manageServers.getAllServers()
    ) { state, servers ->
        val activeServer = when (state) {
            is ConnectionState.Connected -> state.server.name
            else -> "غير متصل"
        }
        HomeUiState(
            connectionState = state,
            activeServerName = activeServer,
            hasServers = servers.isNotEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun toggleConnection() {
        viewModelScope.launch {
            when (uiState.value.connectionState) {
                is ConnectionState.Connected,
                is ConnectionState.Connecting -> disconnectVpn()
                else -> {
                    val servers = manageServers.getAllServers().first()
                    val target = servers.firstOrNull { it.isDefault } ?: servers.firstOrNull()
                    if (target != null) connectToServer(target)
                }
            }
        }
    }
}
