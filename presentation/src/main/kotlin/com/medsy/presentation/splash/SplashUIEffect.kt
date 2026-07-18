package com.medsy.presentation.splash

sealed interface SplashUIEffect {
    data object NavigateToHome : SplashUIEffect
    data object NavigateToOnboarding : SplashUIEffect
    data object NavigateToLogin : SplashUIEffect
}