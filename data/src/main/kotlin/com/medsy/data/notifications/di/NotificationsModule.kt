package com.medsy.data.notifications.di

import com.medsy.data.di.AuthenticatedRetrofit
import com.medsy.data.notifications.datasource.NotificationsRemoteDataSource
import com.medsy.data.notifications.datasource.NotificationsRemoteDataSourceImpl
import com.medsy.data.notifications.remote.NotificationsApi
import com.medsy.data.notifications.repository.NotificationsRepositoryImpl
import com.medsy.domain.notifications.repository.NotificationsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(
        impl: NotificationsRepositoryImpl
    ): NotificationsRepository

    @Binds
    @Singleton
    abstract fun bindNotificationsRemoteDataSource(
        impl: NotificationsRemoteDataSourceImpl
    ): NotificationsRemoteDataSource

    companion object {
        @Provides
        @Singleton
        fun provideNotificationsApi(
            @AuthenticatedRetrofit retrofit: Retrofit
        ): NotificationsApi = retrofit.create(NotificationsApi::class.java)
    }
}
