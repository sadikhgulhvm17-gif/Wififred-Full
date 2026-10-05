package com.wififred.app.domain.usecase.wifi

import com.wififred.app.domain.model.WifiNetwork
import com.wififred.app.domain.repository.WifiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanWifiNetworksUseCase @Inject constructor(
    private val repository: WifiRepository
) {
    operator fun invoke(): Flow<List<WifiNetwork>> =
        repository.scanNearbyNetworks()
}
