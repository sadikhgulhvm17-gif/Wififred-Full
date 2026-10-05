package com.wififred.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class SettingsState(
    val autoReconnect: Boolean = true,
    val killSwitch: Boolean = false,
    val blockAds: Boolean = true,
    val obfuscation: Boolean = false,
    val dnsTunneling: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    fun toggleAutoReconnect() { _state.value = _state.value.copy(autoReconnect = !_state.value.autoReconnect) }
    fun toggleKillSwitch() { _state.value = _state.value.copy(killSwitch = !_state.value.killSwitch) }
    fun toggleBlockAds() { _state.value = _state.value.copy(blockAds = !_state.value.blockAds) }
    fun toggleObfuscation() { _state.value = _state.value.copy(obfuscation = !_state.value.obfuscation) }
    fun toggleDnsTunneling() { _state.value = _state.value.copy(dnsTunneling = !_state.value.dnsTunneling) }
}
