package com.medsy.data.invitation.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InvitationDto(
    @Json(name = "id") val id: Long,
    @Json(name = "pharmacyId") val pharmacyId: Long,
    @Json(name = "pharmacyName") val pharmacyName: String,
    @Json(name = "pharmacistId") val pharmacistId: Long,
    @Json(name = "pharmacistFirstName") val pharmacistFirstName: String,
    @Json(name = "pharmacistLastName") val pharmacistLastName: String,
    @Json(name = "status") val status: String,
    @Json(name = "createdAt") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class InvitePharmacistRequestDto(
    @Json(name = "email") val email: String
)
