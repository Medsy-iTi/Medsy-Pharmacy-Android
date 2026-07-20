package com.medsy.data.invitation.di

import com.medsy.data.invitation.remote.api.InvitationApi
import com.medsy.data.invitation.repository.InvitationRepositoryImpl
import com.medsy.domain.invitation.repository.InvitationRepository
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
abstract class InvitationModule {
    @Binds
    @Singleton
    abstract fun bindInvitationRepository(
        impl: InvitationRepositoryImpl
    ): InvitationRepository

    companion object {
        @Provides
        @Singleton
        fun provideInvitationApi(@AuthenticatedRetrofit retrofit: Retrofit): InvitationApi {
            return retrofit.create(InvitationApi::class.java)
        }
    }
}
