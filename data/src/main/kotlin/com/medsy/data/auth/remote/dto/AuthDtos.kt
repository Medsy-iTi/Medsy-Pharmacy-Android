package com.medsy.data.auth.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
)

@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "phoneNumber") val phoneNumber: String,
    @Json(name = "firstName") val firstName: String,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "password") val password: String,
    @Json(name = "role") val role: String,
    @Json(name = "homeAddress") val homeAddress: String?,
    @Json(name = "dob") val dob: String?,
    @Json(name = "pharmacyId") val pharmacyId: Long?,
)

@JsonClass(generateAdapter = true)
data class VerifyOtpRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "otpCode") val otpCode: String,
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "user") val user: UserResponseDto,
)

@JsonClass(generateAdapter = true)
data class UserResponseDto(
    @Json(name = "id") val id: Long,
    @Json(name = "email") val email: String,
    @Json(name = "firstName") val firstName: String? = null,
    @Json(name = "lastName") val lastName: String? = null,
    @Json(name = "role") val role: String,
    @Json(name = "homeAddress") val homeAddress: String? = null,
    @Json(name = "dob") val dob: String? = null,
)
