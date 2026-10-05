package com.wififred.app.domain.usecase.wifi

import com.wififred.app.domain.model.WifiNetwork
import com.wififred.app.domain.repository.WifiRepository
import javax.inject.Inject

class AssessWifiSecurityUseCase @Inject constructor(
    private val repository: WifiRepository
) {
    operator fun invoke(network: WifiNetwork): Int =
        repository.assessNetworkSecurity(network)
}
