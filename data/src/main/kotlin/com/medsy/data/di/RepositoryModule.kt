package com.medsy.data.di

import com.medsy.data.orders.repository.RequestsRepositoryImpl
import com.medsy.data.products.repository.ProductsRepositoryImpl
import com.medsy.domain.orders.repository.RequestsRepository
import com.medsy.domain.products.repository.ProductsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RequestsDataModule {

    @Binds
    @Singleton
    abstract fun bindRequestsRepository(
        requestsRepositoryImpl: RequestsRepositoryImpl
    ): RequestsRepository

    @Binds
    @Singleton
    abstract fun bindProductsRepository(
        productsRepositoryImpl: ProductsRepositoryImpl
    ): ProductsRepository
}