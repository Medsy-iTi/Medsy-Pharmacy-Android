package com.medsy.data.dashboard.di

import com.medsy.data.dashboard.datasource.DashboardRemoteDataSource
import com.medsy.data.dashboard.datasource.DashboardRemoteDataSourceImpl
import com.medsy.data.dashboard.remote.DashboardApi
import com.medsy.data.dashboard.remote.AiDashboardApi
import com.medsy.data.dashboard.repository.DashboardRepositoryImpl
import com.medsy.data.di.AuthenticatedRetrofit
import com.medsy.data.di.AiAuthenticatedRetrofit
import com.medsy.domain.dashboard.repository.DashboardRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class DashboardModule {
    @Binds
    abstract fun bindDashboardRemoteDataSource(
        implementation: DashboardRemoteDataSourceImpl,
    ): DashboardRemoteDataSource

    @Binds
    abstract fun bindDashboardRepository(
        implementation: DashboardRepositoryImpl,
    ): DashboardRepository

    companion object {
        @Provides
        @JvmStatic
        fun provideDashboardApi(
            @AuthenticatedRetrofit retrofit: Retrofit,
        ): DashboardApi = retrofit.create(DashboardApi::class.java)

        @Provides
        @JvmStatic
        fun provideAiDashboardApi(
            @AiAuthenticatedRetrofit retrofit: Retrofit,
        ): AiDashboardApi = retrofit.create(AiDashboardApi::class.java)
    }
}
