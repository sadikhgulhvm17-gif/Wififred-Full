package com.wififred.app.presentation.screens.servers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wififred.app.domain.model.ServerConfig
import com.wififred.app.domain.model.VpnProtocol
import com.wififred.app.domain.usecase.server.ManageServersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServersUiState(
    val servers: List<ServerConfig> = emptyList(),
    val message: String? = null
)

@HiltViewModel
class ServersViewModel @Inject constructor(
    private val manageServers: ManageServersUseCase
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ServersUiState> = combine(
        manageServers.getAllServers(),
        _message
    ) { servers, msg ->
        ServersUiState(servers = servers, message = msg)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ServersUiState()
    )

    fun addServer(name: String, host: String, port: Int, protocol: VpnProtocol,
                  username: String, password: String) {
        viewModelScope.launch {
            if (name.isBlank() || host.isBlank()) {
                _message.value = "الاسم والعنوان مطلوبان"
                return@launch
            }
            manageServers.add(
                ServerConfig(
                    name = name, host = host, port = port,
                    protocol = protocol, username = username, password = password
                )
            )
            _message.value = "تمت إضافة الخادم بنجاح"
        }
    }

    fun deleteServer(id: Long) {
        viewModelScope.launch {
            manageServers.delete(id)
            _message.value = "تم حذف الخادم"
        }
    }

    fun setDefault(id: Long) {
        viewModelScope.launch {
            manageServers.setDefault(id)
            _message.value = "تم تعيين الخادم الافتراضي"
        }
    }

    fun importConfig(json: String) {
        viewModelScope.launch {
            runCatching { manageServers.import(json) }
                .onSuccess { _message.value = "تم الاستيراد بنجاح" }
                .onFailure { _message.value = "فشل الاستيراد: ${it.message}" }
        }
    }

    fun clearMessage() { _message.value = null }
}
