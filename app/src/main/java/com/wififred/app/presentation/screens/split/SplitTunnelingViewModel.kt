package com.wififred.app.presentation.screens.split

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wififred.app.domain.repository.VpnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val isSelected: Boolean
)

data class SplitUiState(
    val apps: List<InstalledApp> = emptyList(),
    val searchQuery: String = ""
)

@HiltViewModel
class SplitTunnelingViewModel @Inject constructor(
    private val vpnRepository: VpnRepository
) : ViewModel() {

    private val _apps = MutableStateFlow<List<InstalledApp>>(emptyList())
    private val _search = MutableStateFlow("")

    val uiState: StateFlow<SplitUiState> = combine(_apps, _search) { apps, q ->
        val filtered = if (q.isBlank()) apps
        else apps.filter { it.appName.contains(q, ignoreCase = true) }
        SplitUiState(apps = filtered, searchQuery = q)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SplitUiState()
    )

    init {
        // نموذج تجريبي — سيتم استبداله بجلب التطبيقات المثبتة فعلياً
        _apps.value = listOf(
            InstalledApp("com.whatsapp", "WhatsApp", false),
            InstalledApp("com.instagram.android", "Instagram", true),
            InstalledApp("com.google.android.youtube", "YouTube", false),
            InstalledApp("com.telegram.messenger", "Telegram", true),
            InstalledApp("com.facebook.katana", "Facebook", false)
        )
    }

    fun updateSearch(q: String) { _search.value = q }

    fun toggleApp(packageName: String) {
        _apps.value = _apps.value.map {
            if (it.packageName == packageName) it.copy(isSelected = !it.isSelected) else it
        }
        viewModelScope.launch {
            val selected = _apps.value.filter { it.isSelected }.map { it.packageName }
            vpnRepository.setSplitTunneling(selected)
        }
    }
}
