package com.wififred.app.domain.usecase.vpn

import com.wififred.app.domain.model.ServerConfig
import com.wififred.app.domain.repository.VpnRepository
import javax.inject.Inject

class ConnectToServerUseCase @Inject constructor(
    private val repository: VpnRepository
) {
    suspend operator fun invoke(config: ServerConfig) {
        repository.connect(config)
    }
}
