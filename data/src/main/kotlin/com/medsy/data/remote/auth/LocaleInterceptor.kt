package com.medsy.data.remote.auth

import androidx.appcompat.app.AppCompatDelegate
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject

class LocaleInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val language = AppCompatDelegate.getApplicationLocales()[0]?.language
            ?: Locale.getDefault().language
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("lang", language)
            .build()
        return chain.proceed(
            original.newBuilder()
                .url(url)
                .header("Accept-Language", language)
                .build(),
        )
    }
}
