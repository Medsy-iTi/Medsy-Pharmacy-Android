package com.medsy.presentation.auth.registerpharmacy

import androidx.annotation.StringRes

sealed interface PharmacyRegistrationUIEffect {
    data object NavigatePendingApproval : PharmacyRegistrationUIEffect
    data class ShowError(@StringRes val messageRes: Int) : PharmacyRegistrationUIEffect
}
