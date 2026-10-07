package com.wififred.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "wififred_prefs")

class AppPreferences(private val context: Context) {
    companion object {
        val AUTO_RECONNECT = booleanPreferencesKey("auto_reconnect")
        val KILL_SWITCH = booleanPreferencesKey("kill_switch")
        val BLOCK_ADS = booleanPreferencesKey("block_ads")
        val OBFUSCATION = booleanPreferencesKey("obfuscation")
        val SPLIT_APPS = stringPreferencesKey("split_apps")
        val SERVERS_JSON = stringPreferencesKey("servers_json")
    }

    val autoReconnect: Flow<Boolean> = context.dataStore.data.map { it[AUTO_RECONNECT] ?: true }
    val killSwitch: Flow<Boolean> = context.dataStore.data.map { it[KILL_SWITCH] ?: false }
    val blockAds: Flow<Boolean> = context.dataStore.data.map { it[BLOCK_ADS] ?: true }
    val obfuscation: Flow<Boolean> = context.dataStore.data.map { it[OBFUSCATION] ?: false }
    val splitApps: Flow<String> = context.dataStore.data.map { it[SPLIT_APPS] ?: "" }
    val serversJson: Flow<String> = context.dataStore.data.map { it[SERVERS_JSON] ?: "" }

    suspend fun setAutoReconnect(v: Boolean) { context.dataStore.edit { it[AUTO_RECONNECT] = v } }
    suspend fun setKillSwitch(v: Boolean) { context.dataStore.edit { it[KILL_SWITCH] = v } }
    suspend fun setBlockAds(v: Boolean) { context.dataStore.edit { it[BLOCK_ADS] = v } }
    suspend fun setObfuscation(v: Boolean) { context.dataStore.edit { it[OBFUSCATION] = v } }
    suspend fun setSplitApps(v: String) { context.dataStore.edit { it[SPLIT_APPS] = v } }
    suspend fun setServersJson(v: String) { context.dataStore.edit { it[SERVERS_JSON] = v } }
}
