package com.medsy.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.preferences.usecase.CompleteOnboardingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val completeOnboarding: CompleteOnboardingUseCase,
) : ViewModel() {
    private val mutableEffect = Channel<OnboardingUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    fun onIntent(intent: OnboardingUIIntent) {
        when (intent) {
            OnboardingUIIntent.GetStarted -> viewModelScope.launch {
                completeOnboarding()
                mutableEffect.send(OnboardingUIEffect.OpenLogin)
            }
        }
    }
}

sealed interface OnboardingUIIntent {
    data object GetStarted : OnboardingUIIntent
}

sealed interface OnboardingUIEffect {
    data object OpenLogin : OnboardingUIEffect
}
