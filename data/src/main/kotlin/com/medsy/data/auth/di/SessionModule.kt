package com.medsy.data.auth.di

import com.medsy.data.auth.repository.SessionRepositoryImpl
import com.medsy.domain.auth.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {
    @Binds
    abstract fun bindSessionRepository(
        implementation: SessionRepositoryImpl,
    ): SessionRepository
}
