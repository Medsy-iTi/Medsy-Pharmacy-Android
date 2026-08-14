package com.medsy.data.aichat.remote

import com.medsy.data.remote.network.safeApiCall
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AiChatRemoteDataSource @Inject constructor(private val api: AiChatApi) {
    suspend fun sendMessage(request: ChatMessageRequestDto) =
        safeApiCall { api.sendMessage(request) }

    suspend fun sendImage(image: MultipartBody.Part, message: RequestBody?) =
        safeApiCall { api.sendImage(image, message) }

    suspend fun getHistory() = safeApiCall { api.getHistory() }
    suspend fun deleteHistory() = safeApiCall { api.deleteHistory() }
}
