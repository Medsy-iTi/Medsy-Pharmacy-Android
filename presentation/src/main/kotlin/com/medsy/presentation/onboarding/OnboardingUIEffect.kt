package com.medsy.presentation.onboarding

sealed interface OnboardingUIEffect {
    data object NavigateToLogin : OnboardingUIEffect
}
