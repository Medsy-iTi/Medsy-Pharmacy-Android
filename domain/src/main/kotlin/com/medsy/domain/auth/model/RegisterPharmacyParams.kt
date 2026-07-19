package com.medsy.domain.auth.model

data class RegisterPharmacyParams(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phoneNumber: String,
    val license: String
)
