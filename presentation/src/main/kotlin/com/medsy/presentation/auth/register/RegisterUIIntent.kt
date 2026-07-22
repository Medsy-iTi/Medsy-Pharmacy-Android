package com.medsy.presentation.auth.register

import com.medsy.domain.auth.model.AuthUserRole

sealed interface RegisterUIIntent {
    // Personal Data
    data class EmailChanged(val value: String) : RegisterUIIntent
    data class PhoneChanged(val value: String) : RegisterUIIntent
    data class FirstNameChanged(val value: String) : RegisterUIIntent
    data class LastNameChanged(val value: String) : RegisterUIIntent
    data class PasswordChanged(val value: String) : RegisterUIIntent
    data class DobChanged(val value: String) : RegisterUIIntent
    data class RoleChanged(val value: AuthUserRole) : RegisterUIIntent
    data class AddressChanged(val value: String) : RegisterUIIntent
    data class PharmacyIdChanged(val value: Long?) : RegisterUIIntent

    data object Submit : RegisterUIIntent
}
