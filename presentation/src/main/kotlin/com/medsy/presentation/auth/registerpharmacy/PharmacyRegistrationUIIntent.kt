package com.medsy.presentation.auth.registerpharmacy

import androidx.annotation.StringRes

sealed interface PharmacyRegistrationUIIntent {
    data class PharmacyNameChanged(val value: String) : PharmacyRegistrationUIIntent
    data class PhoneNumberChanged(val value: String) : PharmacyRegistrationUIIntent
    data class AddressChanged(val value: String) : PharmacyRegistrationUIIntent

    data class LocationSelected(
        val latitude: Double,
        val longitude: Double,
    ) : PharmacyRegistrationUIIntent

    data class LicenseSelected(
        val displayName: String,
        val mimeType: String?,
        val bytes: ByteArray,
    ) : PharmacyRegistrationUIIntent

    data class LicenseSelectionFailed(
        @StringRes val messageRes: Int,
    ) : PharmacyRegistrationUIIntent

    data object Submit : PharmacyRegistrationUIIntent
}
