package com.wififred.app.di

import com.wififred.app.data.local.dao.InMemoryLogDao
import com.wififred.app.data.local.dao.InMemoryServerDao
import com.wififred.app.data.local.dao.LogDao
import com.wififred.app.data.local.dao.ServerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideServerDao(): ServerDao = InMemoryServerDao()

    @Provides
    @Singleton
    fun provideLogDao(): LogDao = InMemoryLogDao()
}
