package com.medsy.data.remote.auth

import android.util.Log
import com.medsy.data.local.auth.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        Log.d("auth", "Interceptor: Requesting ${original.url}")

        if (original.header("No-Auth") != null) {
            Log.d("auth", "Interceptor: No-Auth header found, stripping headers")
            val stripped = original.newBuilder().removeHeader("No-Auth").build()
            return chain.proceed(stripped)
        }

        val token = tokenStorage.accessToken()
        val request = if (token != null) {
            Log.d("auth", "Interceptor: Adding Authorization header")
            original.newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else {
            Log.d("auth", "Interceptor: No token found in storage")
            original
        }

        val response = chain.proceed(request)
        Log.d("auth", "Interceptor: Response code for ${original.url} is ${response.code}")
        return response
    }
}
