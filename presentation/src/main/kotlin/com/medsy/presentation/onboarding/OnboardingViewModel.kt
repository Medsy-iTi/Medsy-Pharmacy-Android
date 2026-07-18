package com.medsy.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.preferences.usecase.CompleteOnboardingUseCase
import com.medsy.presentation.onboarding.model.OnboardingPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.medsy.presentation.R

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(OnboardingUIState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                _state.value = OnboardingUIState(
                    pages = listOf(
                        OnboardingPage(
                            imageRes = R.drawable.img_onboarding_first,
                            titleRes = R.string.onboarding_title_1,
                            descriptionRes = R.string.onboarding_desc_1
                        ),
                        OnboardingPage(
                            imageRes = R.drawable.img_onboarding_second,
                            titleRes = R.string.onboarding_title_2,
                            descriptionRes = R.string.onboarding_desc_2
                        ),
                        OnboardingPage(
                            imageRes = R.drawable.img_onboarding_third,
                            titleRes = R.string.onboarding_title_3,
                            descriptionRes = R.string.onboarding_desc_3
                        )
                    )
                )
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OnboardingUIState()
        )

    private val mutableEffect = Channel<OnboardingUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    fun onIntent(intent: OnboardingUIIntent) {
        when (intent) {
            OnboardingUIIntent.OnSkipClicked,
            OnboardingUIIntent.OnGetStartedClicked -> {
                viewModelScope.launch {
                    completeOnboardingUseCase()
                    mutableEffect.send(OnboardingUIEffect.NavigateToLogin)
                }
            }
        }
    }
}
