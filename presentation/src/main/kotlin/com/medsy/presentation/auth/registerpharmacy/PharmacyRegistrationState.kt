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
