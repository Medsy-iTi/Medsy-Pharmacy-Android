package com.medsy.data.notifications.remote

import com.medsy.data.notifications.model.DeviceTokenRegistrationDto
import com.medsy.data.notifications.model.NotificationPageDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationsApi {

    @POST("api/v1/pharmacists/me/devices/token")
    suspend fun registerDeviceToken(
        @Body request: DeviceTokenRegistrationDto
    ): Response<ApiResponse<String>>

    @DELETE("api/v1/pharmacists/me/devices/token")
    suspend fun unregisterDeviceToken(
        @Query("fcmToken") fcmToken: String
    ): Response<ApiResponse<String>>

    @PATCH("api/v1/pharmacists/me/notifications/{recipientId}/read")
    suspend fun markNotificationAsRead(
        @Path("recipientId") recipientId: Long
    ): Response<ApiResponse<String>>

    @PATCH("api/v1/pharmacists/me/notifications/read-all")
    suspend fun markAllNotificationsAsRead(): Response<ApiResponse<String>>

    @GET("api/v1/pharmacists/me/notifications")
    suspend fun getNotifications(
        @Query("status") status: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null
    ): Response<ApiResponse<NotificationPageDto>>

    @GET("api/v1/pharmacists/me/notifications/unread-count")
    suspend fun getUnreadNotificationCount(): Response<ApiResponse<Int>>
}
