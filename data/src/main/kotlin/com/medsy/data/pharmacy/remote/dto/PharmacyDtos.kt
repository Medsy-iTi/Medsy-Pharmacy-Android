package com.medsy.data.pharmacy.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PharmacyMineDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "phoneNumber") val phoneNumber: String? = null,
    @Json(name = "isAdmin") val isAdmin: Boolean,
    @Json(name = "pharmacists") val pharmacists: List<PharmacyPharmacistDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PharmacyPharmacistDto(
    @Json(name = "id") val id: Long,
    @Json(name = "firstName") val firstName: String,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "phoneNumber") val phoneNumber: String?,
    @Json(name = "email") val email: String,
    @Json(name = "isAdmin") val isAdmin: Boolean
)

@JsonClass(generateAdapter = true)
data class PharmacyResponseDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "phoneNumber") val phoneNumber: String? = null,
)

@JsonClass(generateAdapter = true)
data class CreatePharmacyRequestDto(
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "address") val address: String? = null,
    @Json(name = "phoneNumber") val phoneNumber: String? = null,
)
