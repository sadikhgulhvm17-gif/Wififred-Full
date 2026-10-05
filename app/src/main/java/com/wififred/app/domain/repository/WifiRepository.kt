package com.wififred.app.domain.repository

import com.wififred.app.domain.model.WifiNetwork
import kotlinx.coroutines.flow.Flow

interface WifiRepository {
    /** يبث قائمة بالشبكات المحيطة بشكل حي */
    fun scanNearbyNetworks(): Flow<List<WifiNetwork>>
    
    /** الشبكة المتصل بها حالياً */
    fun getConnectedNetwork(): Flow<WifiNetwork?>
    
    /** تقييم أمان شبكة معينة */
    fun assessNetworkSecurity(network: WifiNetwork): Int
}
