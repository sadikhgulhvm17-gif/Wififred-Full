package com.wififred.app.data.repository

import android.content.Context
import android.net.wifi.WifiManager
import com.wififred.app.domain.model.SecurityType
import com.wififred.app.domain.model.WifiNetwork
import com.wififred.app.domain.repository.WifiRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiRepositoryImpl @Inject constructor(
    private val context: Context
) : WifiRepository {

    private val wifiManager: WifiManager? =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    override fun scanNearbyNetworks(): Flow<List<WifiNetwork>> = callbackFlow {
        // ملاحظة: الفحص الحقيقي يتطلب أذونات Runtime وسيتم إضافتها لاحقاً
        // هنا نبث قائمة فارغة مبدئياً إلى أن يتم تطبيق المنطق الكامل
        trySend(emptyList())
        awaitClose { /* لا شيء */ }
    }

    override fun getConnectedNetwork(): Flow<WifiNetwork?> = flowOf(null)

    override fun assessNetworkSecurity(network: WifiNetwork): Int = when (network.securityType) {
        SecurityType.WPA3 -> 100
        SecurityType.WPA2 -> 80
        SecurityType.WPA -> 60
        SecurityType.WEP -> 20
        SecurityType.OPEN -> 0
    }
}
