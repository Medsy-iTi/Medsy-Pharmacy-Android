package com.medsy.data.pharmacist.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenRequestDto(
    @Json(name = "fcmToken")
    val fcmToken: String,
    @Json(name = "platform")
    val platform: String = "ANDROID",
    @Json(name = "deviceId")
    val deviceId: String
)
