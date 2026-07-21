package com.medsy.data.auth.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LogoutRequestDto(
    val refreshToken: String,
)
