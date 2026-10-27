package com.glowbook.android.di

import com.glowbook.android.data.repository.ProfessionalsRepositoryImpl
import com.glowbook.android.domain.repository.ProfessionalsRepository
import com.glowbook.android.utils.DefaultDispatcherProvider
import com.glowbook.android.utils.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindProfessionalsRepository(impl: ProfessionalsRepositoryImpl): ProfessionalsRepository

    @Singleton
    @Binds
    abstract fun provideDispatchersProvider(impl: DefaultDispatcherProvider): DispatchersProvider
}
