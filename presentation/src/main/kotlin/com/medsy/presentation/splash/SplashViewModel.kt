package com.medsy.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.usecase.ObserveSessionUseCase
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.common.preferences.model.UserPreferences
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val observeSession: ObserveSessionUseCase,
    private val observePreferences: ObserveUserPreferencesUseCase,
) : ViewModel() {
    private val mutableEffect = Channel<SplashUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            val session = async { observeSession().firstOrNull() }
            val preferences = async { observePreferences().firstOrNull() }
            delay(SplashConstants.MIN_SPLASH_DURATION_MS.milliseconds)

            val currentSession = session.await()
            val currentPreferences = preferences.await()
                ?: UserPreferences(ThemeMode.System, false)
            val destination = when {
                currentPreferences.isOnboardingCompleted.not() -> SplashUIEffect.OpenOnboarding
                currentSession == null -> SplashUIEffect.OpenLogin
                currentSession.account.approvalStatus == PharmacyApprovalStatus.NoPharmacy ->
                    SplashUIEffect.OpenNoPharmacy
                currentSession.account.approvalStatus == PharmacyApprovalStatus.Approved ->
                    SplashUIEffect.OpenHome
                currentSession.account.approvalStatus == PharmacyApprovalStatus.PendingApproval ->
                    SplashUIEffect.OpenPendingApproval
                currentSession.account.approvalStatus == PharmacyApprovalStatus.Rejected ->
                    SplashUIEffect.OpenRejected
                else -> SplashUIEffect.OpenSuspended
            }
            mutableEffect.send(destination)
        }
    }
}

sealed interface SplashUIEffect {
    data object OpenOnboarding : SplashUIEffect
    data object OpenLogin : SplashUIEffect
    data object OpenHome : SplashUIEffect
    data object OpenNoPharmacy : SplashUIEffect
    data object OpenPendingApproval : SplashUIEffect
    data object OpenRejected : SplashUIEffect
    data object OpenSuspended : SplashUIEffect
}
