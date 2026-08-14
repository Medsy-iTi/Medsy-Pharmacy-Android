package com.medsy.data.aichat.remote

import com.medsy.data.remote.network.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AiChatApi {
    @POST("api/v1/ai/chat/messages")
    suspend fun sendMessage(
        @Body request: ChatMessageRequestDto,
    ): Response<ApiResponse<ChatMessageResponseDto>>

    @Multipart
    @POST("api/v1/ai/chat/messages/image")
    suspend fun sendImage(
        @Part image: MultipartBody.Part,
        @Part("message") message: RequestBody?,
    ): Response<ApiResponse<ChatMessageResponseDto>>

    @GET("api/v1/ai/chat/history")
    suspend fun getHistory(): Response<ApiResponse<ChatHistoryResponseDto>>

    @DELETE("api/v1/ai/chat/history")
    suspend fun deleteHistory(): Response<ApiResponse<ChatHistoryResponseDto>>
}
