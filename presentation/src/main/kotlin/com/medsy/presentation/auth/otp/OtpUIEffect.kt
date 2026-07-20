package com.medsy.presentation.auth.otp

sealed interface OtpUIEffect {
    data object NavigateNoPharmacy : OtpUIEffect
    data class ShowError(val messageRes: Int) : OtpUIEffect
}
