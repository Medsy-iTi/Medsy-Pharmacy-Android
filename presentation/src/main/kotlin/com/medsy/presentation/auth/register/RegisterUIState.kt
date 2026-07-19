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
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val emailErrorRes: Int? = null,
    val phoneErrorRes: Int? = null,
    val firstNameErrorRes: Int? = null,
    val lastNameErrorRes: Int? = null,
    val passwordErrorRes: Int? = null,
)
