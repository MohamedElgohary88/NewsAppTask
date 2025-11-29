@file:Suppress("unused")

package com.elgohary.newsapptask.di

import com.elgohary.newsapptask.data.repository.NewsRepositoryImpl
import com.elgohary.newsapptask.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds domain repository interface to its data implementation. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository
}
