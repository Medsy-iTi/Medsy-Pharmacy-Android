package com.medsy.presentation.auth.register

import com.medsy.domain.auth.model.AuthUserRole

data class RegisterUIState(
    val email: String = "",
    val phoneNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val dob: String = "",
    val role: AuthUserRole = AuthUserRole.PHARMACIST,
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
