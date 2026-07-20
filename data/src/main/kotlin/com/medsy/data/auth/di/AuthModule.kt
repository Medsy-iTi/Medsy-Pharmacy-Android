package com.medsy.data.auth.di

import com.medsy.data.auth.remote.api.AuthApi
import com.medsy.data.auth.repository.AuthRepositoryImpl
import com.medsy.data.di.AuthenticatedRetrofit
import com.medsy.domain.auth.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl,
    ): AuthRepository

    companion object {
        @Provides
        @Singleton
        @JvmStatic
        fun provideAuthApi(
            @AuthenticatedRetrofit retrofit: Retrofit,
        ): AuthApi = retrofit.create(AuthApi::class.java)
    }
}
