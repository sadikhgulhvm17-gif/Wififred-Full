package com.wififred.app.presentation.screens.wifi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wififred.app.domain.model.WifiNetwork
import com.wififred.app.domain.usecase.wifi.ScanWifiNetworksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WifiUiState(
    val networks: List<WifiNetwork> = emptyList(),
    val isScanning: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WifiViewModel @Inject constructor(
    private val scanUseCase: ScanWifiNetworksUseCase
) : ViewModel() {

    private val _isScanning = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<WifiUiState> = combine(
        scanUseCase(),
        _isScanning,
        _error
    ) { networks, scanning, err ->
        WifiUiState(
            networks = networks.sortedByDescending { it.signalStrength },
            isScanning = scanning,
            error = err
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WifiUiState()
    )

    fun scan() {
        viewModelScope.launch {
            _isScanning.value = true
            kotlinx.coroutines.delay(1500) // محاكاة الفحص
            _isScanning.value = false
        }
    }
}
