package com.medsy.presentation.auth.register

import com.medsy.domain.auth.model.Role

data class RegisterUIState(
    val email: String = "",
    val phoneNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val dob: String = "",
    val role: Role = Role.PHARMACIST,
    val homeAddress: String = "",
    val pharmacyId: Long? = null,

    val pharmacyName: String = "",
    val pharmacyPhoneNumber: String = "",
    val licenseNumber: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val pharmacyAddress: String = "",

    val currentStep: Int = 1,
    val isLoading: Boolean = false,

    val emailErrorRes: Int? = null,
    val phoneErrorRes: Int? = null,
    val firstNameErrorRes: Int? = null,
    val lastNameErrorRes: Int? = null,
    val passwordErrorRes: Int? = null,
    val pharmacyNameErrorRes: Int? = null,
    val pharmacyPhoneErrorRes: Int? = null,
    val licenseNumberErrorRes: Int? = null,
    val addressErrorRes: Int? = null,
)
