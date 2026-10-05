package com.wififred.app.di

import com.wififred.app.data.repository.ServerRepositoryImpl
import com.wififred.app.data.repository.StatsRepositoryImpl
import com.wififred.app.data.repository.VpnRepositoryImpl
import com.wififred.app.data.repository.WifiRepositoryImpl
import com.wififred.app.domain.repository.ServerRepository
import com.wififred.app.domain.repository.StatsRepository
import com.wififred.app.domain.repository.VpnRepository
import com.wififred.app.domain.repository.WifiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindServerRepository(impl: ServerRepositoryImpl): ServerRepository

    @Binds
    @Singleton
    abstract fun bindVpnRepository(impl: VpnRepositoryImpl): VpnRepository

    @Binds
    @Singleton
    abstract fun bindWifiRepository(impl: WifiRepositoryImpl): WifiRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(impl: StatsRepositoryImpl): StatsRepository
}
