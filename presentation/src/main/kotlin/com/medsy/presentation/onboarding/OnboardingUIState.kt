package com.medsy.presentation.onboarding

import com.medsy.presentation.onboarding.model.OnboardingPage

data class OnboardingUIState (
    val pages: List<OnboardingPage> = emptyList()

)