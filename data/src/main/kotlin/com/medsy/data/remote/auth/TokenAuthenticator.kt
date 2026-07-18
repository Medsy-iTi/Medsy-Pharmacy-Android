package com.medsy.data.remote.auth

import com.medsy.data.local.auth.TokenStorage
import com.medsy.data.remote.auth.api.RefreshApi
import com.medsy.data.remote.auth.dto.RefreshRequestDto
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val refreshApi: RefreshApi,
) : Authenticator {
    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_ATTEMPTS) {
            tokenStorage.clear()
            return null
        }

        val failedToken = response.request.header("Authorization")
            ?.removePrefix("Bearer ")

        return runBlocking {
            mutex.withLock {
                val currentToken = tokenStorage.accessToken()
                if (currentToken != null && currentToken != failedToken) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                val refreshToken = tokenStorage.refreshToken() ?: run {
                    tokenStorage.clear()
                    return@runBlocking null
                }

                val refreshResponse = runCatching {
                    refreshApi.refresh(RefreshRequestDto(refreshToken))
                }.getOrNull()
                val body = refreshResponse?.body()
                val tokens = body?.data

                if (
                    refreshResponse == null ||
                    !refreshResponse.isSuccessful ||
                    body?.success != true ||
                    tokens == null
                ) {
                    tokenStorage.clear()
                    return@runBlocking null
                }

                tokenStorage.updateTokens(tokens.accessToken, tokens.refreshToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${tokens.accessToken}")
                    .build()
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count += 1
            prior = prior.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_ATTEMPTS = 2
    }
}
