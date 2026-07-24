package com.medsy.data.pharmacist.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PresenceDto(
    @Json(name = "onDuty") val onDuty: Boolean?,
    @Json(name = "lastHeartbeatAt") val lastHeartbeatAt: String?
)
