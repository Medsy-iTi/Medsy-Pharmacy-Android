package com.medsy.presentation.auth.otp

sealed interface OtpUIEffect {
    data object NavigateToProfessionalInfo : OtpUIEffect
    data class ShowError(val messageRes: Int) : OtpUIEffect
}
