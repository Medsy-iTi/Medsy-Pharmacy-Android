package com.medsy.data.di

import com.medsy.data.BuildConfig
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.auth.AuthInterceptor
import com.medsy.data.remote.auth.LocaleInterceptor
import com.medsy.data.remote.auth.TokenAuthenticator
import com.medsy.data.remote.auth.api.AuthApi
import com.medsy.data.remote.auth.api.RefreshApi
import com.medsy.data.remote.pharmacy.api.PharmacyApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshClient(
        localeInterceptor: LocaleInterceptor,
        authInterceptor: AuthInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(localeInterceptor)
        .addInterceptor(authInterceptor)
        .addSafeLogging()
        .build()

    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideRefreshRetrofit(
        moshi: Moshi,
        @RefreshClient client: OkHttpClient,
    ): Retrofit = retrofit(moshi, client)

    @Provides
    @Singleton
    fun provideRefreshApi(
        @RefreshRetrofit retrofit: Retrofit,
    ): RefreshApi = retrofit.create(RefreshApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(
        @RefreshRetrofit retrofit: Retrofit,
    ): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    @AuthenticatedClient
    fun provideAuthenticatedClient(
        localeInterceptor: LocaleInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(localeInterceptor)
        .addInterceptor(authInterceptor)
        .authenticator(tokenAuthenticator)
        .addSafeLogging()
        .build()

    @Provides
    @Singleton
    @AuthenticatedRetrofit
    fun provideAuthenticatedRetrofit(
        moshi: Moshi,
        @AuthenticatedClient client: OkHttpClient,
    ): Retrofit = retrofit(moshi, client)

    @Provides
    @Singleton
    fun provideApiService(
        @AuthenticatedRetrofit retrofit: Retrofit,
    ): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun providePharmacyApi(
        @AuthenticatedRetrofit retrofit: Retrofit,
    ): PharmacyApi =
        retrofit.create(PharmacyApi::class.java)

    private fun retrofit(moshi: Moshi, client: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private fun OkHttpClient.Builder.addSafeLogging(): OkHttpClient.Builder = apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.HEADERS
                    redactHeader("Authorization")
                    redactHeader("Cookie")
                    redactHeader("Set-Cookie")
                },
            )
        }
    }
}
