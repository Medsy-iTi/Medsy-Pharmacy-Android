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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.medsy.domain.common.fold
import com.medsy.domain.common.MedsyError
import com.medsy.domain.pharmacist.model.Pharmacist
import com.medsy.domain.pharmacist.usecase.GetCurrentPharmacistUseCase
import com.medsy.domain.pharmacist.usecase.SetPharmacistPresenceUseCase
import com.medsy.domain.common.preferences.usecase.SetReceivingOrdersPreferenceUseCase
import com.medsy.domain.common.preferences.usecase.SetReceivingNotificationsPreferenceUseCase
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import com.medsy.domain.pharmacy.usecase.ObserveMyPharmacyUseCase
import android.util.Log
import com.medsy.domain.pharmacy.model.MyPharmacy
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
    private val observeMyPharmacy: ObserveMyPharmacyUseCase,
    private val setReceivingOrdersPreference: SetReceivingOrdersPreferenceUseCase,
    private val setReceivingNotificationsPreference: SetReceivingNotificationsPreferenceUseCase,
) : ViewModel() {

    private val isLoadingFlow = MutableStateFlow(false)
    private val isPresenceSwitchLoadingFlow = MutableStateFlow(false)
    private val isNotificationSwitchLoadingFlow = MutableStateFlow(false)
    private val pharmacistFlow = MutableStateFlow<Pharmacist?>(null)
    private val errorFlow = MutableStateFlow<MedsyError?>(null)
    private val isLoggingOutFlow = MutableStateFlow(false)
    private val isAvatarSheetOpenFlow = MutableStateFlow(false)
    private val showLogoutDialogFlow = MutableStateFlow(false)

    private val dataFlow = combine(
        isLoadingFlow,
        pharmacistFlow,
        observeMyPharmacy(),
        errorFlow
    ) { isLoading, pharmacist, pharmacy, error ->
        DataState(isLoading, pharmacist, pharmacy, error)
    }

    private val uiFlagsFlow = combine(
        isLoggingOutFlow,
        isAvatarSheetOpenFlow,
        showLogoutDialogFlow,
        isPresenceSwitchLoadingFlow,
        isNotificationSwitchLoadingFlow
    ) { isLoggingOut, isAvatarSheetOpen, showLogoutDialog, isPresenceSwitchLoading, isNotificationSwitchLoading ->
        UiFlags(isLoggingOut, isAvatarSheetOpen, showLogoutDialog, isPresenceSwitchLoading, isNotificationSwitchLoading)
    }

    val state = combine(
        observePreferences(),
        uiFlagsFlow,
        dataFlow
    ) { prefs, uiFlags, data ->
        ProfileState(
            themeMode = prefs.themeMode,
            isReceivingOrders = prefs.isReceivingOrders,
            isReceivingNotifications = prefs.isReceivingNotifications,
            isLoading = data.isLoading,
            pharmacist = data.pharmacist,
            pharmacy = data.pharmacy,
            error = data.error,
            isAvatarFemale = prefs.isAvatarFemale,
            isLoggingOut = uiFlags.isLoggingOut,
            isAvatarSheetOpen = uiFlags.isAvatarSheetOpen,
            showLogoutDialog = uiFlags.showLogoutDialog,
            isPresenceSwitchLoading = uiFlags.isPresenceSwitchLoading,
            isNotificationSwitchLoading = uiFlags.isNotificationSwitchLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProfileState(),
    )

    init {
        loadProfileData()
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
                onSuccess = { /* Data flows through observeMyPharmacy */ },
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
            is ProfileUIIntent.ReceivingNotificationsChanged -> viewModelScope.launch {
                isNotificationSwitchLoadingFlow.value = true
                setReceivingNotificationsPreference(intent.isReceiving)
                
                // Small delay to make the switch feel responsive but indicate work is done
                delay(200.milliseconds)
                isNotificationSwitchLoadingFlow.value = false
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
        val isPresenceSwitchLoading: Boolean,
        val isNotificationSwitchLoading: Boolean
    )
}
