package com.medsy.presentation.auth.login

sealed interface LoginUIIntent {
    data class EmailChanged(val value: String) : LoginUIIntent
    data class PasswordChanged(val value: String) : LoginUIIntent
    data object LoginWithGoogle : LoginUIIntent
    data object Submit : LoginUIIntent
}
