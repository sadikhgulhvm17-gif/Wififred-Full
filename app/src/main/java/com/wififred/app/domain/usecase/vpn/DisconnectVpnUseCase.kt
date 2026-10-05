package com.wififred.app.domain.usecase.vpn

import com.wififred.app.domain.repository.VpnRepository
import javax.inject.Inject

class DisconnectVpnUseCase @Inject constructor(
    private val repository: VpnRepository
) {
    suspend operator fun invoke() = repository.disconnect()
}
