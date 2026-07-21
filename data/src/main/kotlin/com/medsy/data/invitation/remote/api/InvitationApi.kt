package com.medsy.data.invitation.remote.api

import com.medsy.data.invitation.remote.dto.InvitationDto
import com.medsy.data.invitation.remote.dto.InvitePharmacistRequestDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface InvitationApi {
    @GET("api/v1/pharmacy-invitations/pharmacy/{pharmacyId}")
    suspend fun getPendingInvitationsForPharmacy(@Path("pharmacyId") pharmacyId: Long): Response<ApiResponse<List<InvitationDto>>>

    @POST("api/v1/pharmacy-invitations/pharmacy/{pharmacyId}")
    suspend fun invitePharmacist(
        @Path("pharmacyId") pharmacyId: Long,
        @Body request: InvitePharmacistRequestDto
    ): Response<ApiResponse<InvitationDto>>

    @PATCH("api/v1/pharmacy-invitations/{id}/decline")
    suspend fun declineInvitation(@Path("id") id: Long): Response<ApiResponse<InvitationDto>>

    @PATCH("api/v1/pharmacy-invitations/{id}/accept")
    suspend fun acceptInvitation(@Path("id") id: Long): Response<ApiResponse<InvitationDto>>

    @GET("api/v1/pharmacy-invitations/me")
    suspend fun getMyPendingInvitations(): Response<ApiResponse<List<InvitationDto>>>

    @GET("api/v1/pharmacy-invitations/admin")
    suspend fun getAdminPendingInvitations(): Response<ApiResponse<List<InvitationDto>>>

    @DELETE("api/v1/pharmacy-invitations/{id}")
    suspend fun deleteInvitation(@Path("id") id: Long): Response<ApiResponse<String>>
}
