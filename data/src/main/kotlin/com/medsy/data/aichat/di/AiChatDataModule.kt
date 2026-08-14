package com.medsy.data.aichat.di

import com.medsy.data.aichat.remote.AiChatApi
import com.medsy.data.aichat.repository.AiChatRepositoryImpl
import com.medsy.data.di.AiAuthenticatedRetrofit
import com.medsy.domain.aichat.repository.AiChatRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiChatDataModule {
    @Binds
    @Singleton
    abstract fun bindRepository(implementation: AiChatRepositoryImpl): AiChatRepository

    companion object {
        @Provides
        @JvmStatic
        fun provideApi(@AiAuthenticatedRetrofit retrofit: Retrofit): AiChatApi =
            retrofit.create(AiChatApi::class.java)
    }
}
