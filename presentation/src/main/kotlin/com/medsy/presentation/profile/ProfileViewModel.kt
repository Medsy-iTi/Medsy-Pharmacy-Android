package com.medsy.presentation.profile

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.ClearSessionUseCase
import com.medsy.domain.auth.usecase.LogoutUseCase
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import com.medsy.domain.common.preferences.usecase.SetThemeModeUseCase
import com.medsy.domain.common.preferences.usecase.SetAvatarGenderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.medsy.domain.common.fold
import com.medsy.domain.common.MedsyError
import com.medsy.domain.pharmacist.model.Pharmacist
import com.medsy.domain.pharmacist.usecase.GetCurrentPharmacistUseCase
import com.medsy.domain.pharmacist.usecase.SetPharmacistPresenceUseCase
import com.medsy.domain.pharmacist.usecase.SendHeartbeatUseCase
import com.medsy.domain.common.preferences.usecase.SetReceivingOrdersPreferenceUseCase
import com.medsy.domain.pharmacy.model.MyPharmacy
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import android.util.Log
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observePreferences: ObserveUserPreferencesUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    private val setAvatarGender: SetAvatarGenderUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentPharmacist: GetCurrentPharmacistUseCase,
    private val setPharmacistPresence: SetPharmacistPresenceUseCase,
    private val getMyPharmacy: GetMyPharmacyUseCase,
    private val sendHeartbeat: SendHeartbeatUseCase,
    private val setReceivingOrdersPreference: SetReceivingOrdersPreferenceUseCase,
) : ViewModel() {

    private var heartbeatJob: kotlinx.coroutines.Job? = null


    private val isLoadingFlow = MutableStateFlow(false)
    private val isPresenceSwitchLoadingFlow = MutableStateFlow(false)
    private val pharmacistFlow = MutableStateFlow<Pharmacist?>(null)
    private val pharmacyFlow = MutableStateFlow<MyPharmacy?>(null)
    private val errorFlow = MutableStateFlow<MedsyError?>(null)
    private val isLoggingOutFlow = MutableStateFlow(false)
    private val isAvatarSheetOpenFlow = MutableStateFlow(false)
    private val showLogoutDialogFlow = MutableStateFlow(false)

    private val dataFlow = combine(
        isLoadingFlow,
        pharmacistFlow,
        pharmacyFlow,
        errorFlow
    ) { isLoading, pharmacist, pharmacy, error ->
        DataState(isLoading, pharmacist, pharmacy, error)
    }

    private val uiFlagsFlow = combine(
        isLoggingOutFlow,
        isAvatarSheetOpenFlow,
        showLogoutDialogFlow,
        isPresenceSwitchLoadingFlow
    ) { isLoggingOut, isAvatarSheetOpen, showLogoutDialog, isPresenceSwitchLoading ->
        UiFlags(isLoggingOut, isAvatarSheetOpen, showLogoutDialog, isPresenceSwitchLoading)
    }

    val state = combine(
        observePreferences(),
        uiFlagsFlow,
        dataFlow
    ) { prefs, uiFlags, data ->
        ProfileState(
            themeMode = prefs.themeMode,
            isReceivingOrders = prefs.isReceivingOrders,
            isLoading = data.isLoading,
            pharmacist = data.pharmacist,
            pharmacy = data.pharmacy,
            error = data.error,
            isAvatarFemale = prefs.isAvatarFemale,
            isLoggingOut = uiFlags.isLoggingOut,
            isAvatarSheetOpen = uiFlags.isAvatarSheetOpen,
            showLogoutDialog = uiFlags.showLogoutDialog,
            isPresenceSwitchLoading = uiFlags.isPresenceSwitchLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProfileState(),
    )

    init {
        loadProfileData()
        observePresence()
    }

    private fun observePresence() {
        viewModelScope.launch {
            observePreferences().collect { prefs ->
                if (prefs.isReceivingOrders) {
                    startHeartbeat()
                } else {
                    stopHeartbeat()
                }
            }
        }
    }

    private fun startHeartbeat() {
        if (heartbeatJob?.isActive == true) return
        heartbeatJob = viewModelScope.launch {
            while (true) {
                val result = sendHeartbeat()
                result.fold(
                    onSuccess = { status ->
                        Log.d("PharmacistPresence", "Heartbeat sent: onDuty=${status.onDuty}")
                        if (!status.onDuty) {
                            setReceivingOrdersPreference(false)
                        }
                    },
                    onError = { error ->
                        Log.e("PharmacistPresence", "Heartbeat failed: $error")
                    }
                )
                delay(60_000.milliseconds)
            }
        }
    }

    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    private fun loadProfileData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val loadingJob = launch {
                delay(100.milliseconds)
                isLoadingFlow.value = true
                errorFlow.value = null
            }
            
            val pharmacistResult = getCurrentPharmacist(forceRefresh)
            pharmacistResult.fold(
                onSuccess = { pharmacistFlow.value = it },
                onError = { errorFlow.value = it }
            )

            val pharmacyResult = getMyPharmacy(forceRefresh)
            loadingJob.cancel()
            
            pharmacyResult.fold(
                onSuccess = { pharmacyFlow.value = it },
                onError = { errorFlow.value = it }
            )

            isLoadingFlow.value = false
        }
    }

    private val mutableEffect = Channel<ProfileUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    fun onIntent(intent: ProfileUIIntent) {
        when (intent) {
            is ProfileUIIntent.ThemeChanged -> viewModelScope.launch {
                setThemeMode(intent.themeMode)
            }
            is ProfileUIIntent.ToggleAvatarGender -> viewModelScope.launch {
                setAvatarGender(intent.isFemale)
            }
            is ProfileUIIntent.LanguageChanged -> {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(intent.languageTag),
                )
            }
            is ProfileUIIntent.ReceivingStatusChanged -> viewModelScope.launch {
                isPresenceSwitchLoadingFlow.value = true
                val result = setPharmacistPresence(intent.isReceiving)
                result.fold(
                    onSuccess = { status ->
                        Log.d("PharmacistPresence", "Presence updated successfully: onDuty=${status.onDuty}, lastHeartbeatAt=${status.lastHeartbeatAt}")
                        setReceivingOrdersPreference(status.onDuty)
                    },
                    onError = { error ->
                        Log.e("PharmacistPresence", "Failed to update presence: $error")
                    }
                )
                isPresenceSwitchLoadingFlow.value = false
            }
            is ProfileUIIntent.OpenAvatarSheet -> {
                isAvatarSheetOpenFlow.value = true
            }
            is ProfileUIIntent.CloseAvatarSheet -> {
                isAvatarSheetOpenFlow.value = false
            }
            is ProfileUIIntent.ShowLogoutDialog -> {
                showLogoutDialogFlow.value = true
            }
            is ProfileUIIntent.HideLogoutDialog -> {
                showLogoutDialogFlow.value = false
            }
            is ProfileUIIntent.Logout -> viewModelScope.launch {
                showLogoutDialogFlow.value = false
                isLoggingOutFlow.value = true
                logoutUseCase()
                isLoggingOutFlow.value = false
                mutableEffect.send(ProfileUIEffect.OpenLogin)
            }
            ProfileUIIntent.NavigateToInvitePharmacist -> viewModelScope.launch {
                mutableEffect.send(ProfileUIEffect.OpenInvitePharmacist)
            }
            ProfileUIIntent.NavigateToPharmacistsList -> viewModelScope.launch {
                mutableEffect.send(ProfileUIEffect.OpenPharmacistsList)
            }
            ProfileUIIntent.NavigateToPersonalInfo -> viewModelScope.launch {
                mutableEffect.send(ProfileUIEffect.OpenPersonalInfo)
            }
            ProfileUIIntent.Refresh -> {
                loadProfileData(forceRefresh = true)
            }
        }
    }

    private data class DataState(
        val isLoading: Boolean,
        val pharmacist: Pharmacist?,
        val pharmacy: MyPharmacy?,
        val error: MedsyError?
    )

    private data class UiFlags(
        val isLoggingOut: Boolean,
        val isAvatarSheetOpen: Boolean,
        val showLogoutDialog: Boolean,
        val isPresenceSwitchLoading: Boolean
    )
}
