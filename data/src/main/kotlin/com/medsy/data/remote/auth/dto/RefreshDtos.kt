package com.medsy.data.remote.auth.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshRequestDto(
    @Json(name = "refreshToken") val refreshToken: String,
)

@JsonClass(generateAdapter = true)
data class RefreshTokenDto(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
)
