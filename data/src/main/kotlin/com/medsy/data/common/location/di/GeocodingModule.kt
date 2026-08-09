package com.medsy.data.common.location.di

import com.medsy.data.common.location.repo.GeocodingRepositoryImpl
import com.medsy.domain.common.location.GeocodingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GeocodingModule {

    @Binds
    abstract fun bindGeocodingRepo(
        geocodingRepositoryImpl: GeocodingRepositoryImpl
    ): GeocodingRepository

}