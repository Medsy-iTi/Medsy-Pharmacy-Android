package com.medsy.data.pharmacy.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdatePharmacyRequestDto(
    val name: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val phoneNumber: String?
)
