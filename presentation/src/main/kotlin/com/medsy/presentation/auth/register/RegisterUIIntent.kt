package com.medsy.presentation.auth.register

import com.medsy.domain.auth.model.Role

sealed interface RegisterUIIntent {
    // Personal Data
    data class EmailChanged(val value: String) : RegisterUIIntent
    data class PhoneChanged(val value: String) : RegisterUIIntent
    data class FirstNameChanged(val value: String) : RegisterUIIntent
    data class LastNameChanged(val value: String) : RegisterUIIntent
    data class PasswordChanged(val value: String) : RegisterUIIntent
    data class DobChanged(val value: String) : RegisterUIIntent
    data class RoleChanged(val value: Role) : RegisterUIIntent
    data class AddressChanged(val value: String) : RegisterUIIntent
    data class PharmacyIdChanged(val value: Long?) : RegisterUIIntent
    
    // pharmacyInfo
    data class PharmacyNameChanged(val value: String) : RegisterUIIntent
    data class PharmacyPhoneChanged(val value: String) : RegisterUIIntent
    data class LicenseNumberChanged(val value: String) : RegisterUIIntent
    data class LocationPicked(val lat: Double, val lng: Double, val address: String) : RegisterUIIntent
    data class PharmacyAddressChanged(val value: String) : RegisterUIIntent

    data object Submit : RegisterUIIntent
    data object SubmitPharmacyInfo : RegisterUIIntent
}
