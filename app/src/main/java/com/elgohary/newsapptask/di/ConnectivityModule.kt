@file:Suppress("unused")

package com.elgohary.newsapptask.di

import com.elgohary.newsapptask.core.ConnectivityObserver
import com.elgohary.newsapptask.core.NetworkConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds ConnectivityObserver to its Network implementation. */
@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {
    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(impl: NetworkConnectivityObserver): ConnectivityObserver
}
