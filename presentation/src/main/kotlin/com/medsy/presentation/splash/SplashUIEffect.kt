package com.medsy.presentation.splash

sealed interface SplashUIEffect {
    data object OpenOnboarding : SplashUIEffect
    data object OpenLogin : SplashUIEffect
    data object OpenHome : SplashUIEffect
    data object OpenPendingApproval : SplashUIEffect
    data object OpenRejected : SplashUIEffect
    data object OpenSuspended : SplashUIEffect
}
