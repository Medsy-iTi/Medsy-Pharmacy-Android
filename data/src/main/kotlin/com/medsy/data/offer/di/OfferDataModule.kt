package com.medsy.data.offer.di

import com.medsy.data.offer.remote.api.OfferApi
import com.medsy.data.offer.repository.OfferRepositoryImpl
import com.medsy.domain.offer.repository.OfferRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OfferDataModule {

    @Binds
    @Singleton
    abstract fun bindOfferRepository(
        impl: OfferRepositoryImpl
    ): OfferRepository

    companion object {
        @Provides
        @Singleton
        fun provideOfferApi(retrofit: Retrofit): OfferApi {
            return retrofit.create(OfferApi::class.java)
        }
    }
}
