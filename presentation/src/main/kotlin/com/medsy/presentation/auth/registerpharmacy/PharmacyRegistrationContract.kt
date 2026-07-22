package com.medsy.presentation.auth.registerpharmacy

import androidx.annotation.StringRes

data class PharmacyRegistrationState(
    val pharmacyName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val selectedLatitude: Double? = null,
    val selectedLongitude: Double? = null,
    val licenseName: String? = null,
    val licenseSizeKb: Int? = null,
    val isSubmitting: Boolean = false,
    @StringRes val pharmacyNameErrorRes: Int? = null,
    @StringRes val locationErrorRes: Int? = null,
    @StringRes val licenseErrorRes: Int? = null,
)

sealed interface PharmacyRegistrationIntent {
    data class PharmacyNameChanged(val value: String) : PharmacyRegistrationIntent
    data class PhoneNumberChanged(val value: String) : PharmacyRegistrationIntent
    data class AddressChanged(val value: String) : PharmacyRegistrationIntent

    data class LocationSelected(
        val latitude: Double,
        val longitude: Double,
    ) : PharmacyRegistrationIntent

    data class LicenseSelected(
        val displayName: String,
        val mimeType: String?,
        val bytes: ByteArray,
    ) : PharmacyRegistrationIntent

    data class LicenseSelectionFailed(
        @StringRes val messageRes: Int,
    ) : PharmacyRegistrationIntent

    data object Submit : PharmacyRegistrationIntent
}

sealed interface PharmacyRegistrationEffect {
    data object NavigatePendingApproval : PharmacyRegistrationEffect
    data class ShowError(@StringRes val messageRes: Int) : PharmacyRegistrationEffect
}
