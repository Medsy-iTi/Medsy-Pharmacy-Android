package com.medsy.data.pharmacist.di

import com.medsy.data.pharmacist.remote.api.PharmacistApi
import com.medsy.data.pharmacist.repository.PharmacistRepositoryImpl
import com.medsy.domain.pharmacist.repository.PharmacistRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.medsy.data.di.AuthenticatedRetrofit
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PharmacistModule {
    @Binds
    @Singleton
    abstract fun bindPharmacistRepository(
        impl: PharmacistRepositoryImpl
    ): PharmacistRepository

    companion object {
        @Provides
        @Singleton
        fun providePharmacistApi(@AuthenticatedRetrofit retrofit: Retrofit): PharmacistApi {
            return retrofit.create(PharmacistApi::class.java)
        }
    }
}
