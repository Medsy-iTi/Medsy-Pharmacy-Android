package com.medsy.presentation.onboarding

sealed interface OnboardingUIIntent {
    data object OnGetStartedClicked : OnboardingUIIntent
    data object OnSkipClicked : OnboardingUIIntent

}