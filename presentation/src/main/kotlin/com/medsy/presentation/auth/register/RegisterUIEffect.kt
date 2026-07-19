package com.medsy.presentation.auth.register

sealed interface RegisterUIEffect {
    data class NavigateToOtp(val email: String) : RegisterUIEffect
    data object NavigateToDocuments : RegisterUIEffect
    data class ShowError(val messageRes: Int) : RegisterUIEffect
}
