package com.example.stackoverflow.core.network.di

import com.example.stackoverflow.core.network.connectivity.ConnectivityNetworkMonitor
import com.example.stackoverflow.core.network.connectivity.NetworkMonitor
import com.example.stackoverflow.core.network.repository.DataStoreRecentSearchesRepository
import com.example.stackoverflow.core.network.repository.RecentSearchesRepository
import com.example.stackoverflow.core.network.repository.StackOverflowRepository
import com.example.stackoverflow.core.network.repository.StackOverflowRepositoryImpl
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
    abstract fun bindStackOverflowRepository(
        impl: StackOverflowRepositoryImpl,
    ): StackOverflowRepository

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        impl: ConnectivityNetworkMonitor,
    ): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindRecentSearchesRepository(
        impl: DataStoreRecentSearchesRepository,
    ): RecentSearchesRepository
}
