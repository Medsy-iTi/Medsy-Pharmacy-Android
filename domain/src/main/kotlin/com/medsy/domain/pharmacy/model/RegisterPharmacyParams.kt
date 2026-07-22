package com.medsy.domain.pharmacy.model

data class RegisterPharmacyParams(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val phoneNumber: String?,
    val licensePdfBytes: ByteArray,
)
