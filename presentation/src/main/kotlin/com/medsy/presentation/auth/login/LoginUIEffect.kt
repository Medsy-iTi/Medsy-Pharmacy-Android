package com.medsy.presentation.auth.login

sealed interface LoginUIEffect {
    data object NavigateHome : LoginUIEffect
    data object NavigateNoPharmacy : LoginUIEffect
    data class ShowError(val messageRes: Int) : LoginUIEffect
}
