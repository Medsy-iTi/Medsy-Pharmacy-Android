package com.medsy.data.di

import com.medsy.data.orders.datasource.OrdersLocalDataSource
import com.medsy.data.orders.datasource.OrdersLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataSourcesModule {

    @Binds
    @Singleton
    abstract fun bindOrdersLocalDataSource(
        impl: OrdersLocalDataSourceImpl
    ): OrdersLocalDataSource
}