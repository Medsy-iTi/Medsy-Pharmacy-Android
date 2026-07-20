package com.medsy.data.pharmacy.di

import com.medsy.data.di.AuthenticatedRetrofit
import com.medsy.data.pharmacy.remote.api.PharmacyApi
import com.medsy.data.pharmacy.repository.PharmacyRepositoryImpl
import com.medsy.domain.pharmacy.repository.PharmacyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PharmacyModule {
    @Binds
    abstract fun bindPharmacyRepository(
        implementation: PharmacyRepositoryImpl,
    ): PharmacyRepository

    companion object {
        @Provides
        @Singleton
        @JvmStatic
        fun providePharmacyApi(
            @AuthenticatedRetrofit retrofit: Retrofit,
        ): PharmacyApi = retrofit.create(PharmacyApi::class.java)
    }
}
