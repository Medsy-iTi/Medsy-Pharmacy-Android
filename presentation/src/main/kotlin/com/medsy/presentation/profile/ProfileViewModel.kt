package com.medsy.presentation.profile

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.ClearSessionUseCase
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import com.medsy.domain.common.preferences.usecase.SetThemeModeUseCase
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
import com.medsy.domain.pharmacy.model.MyPharmacy
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observePreferences: ObserveUserPreferencesUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    private val clearSession: ClearSessionUseCase,
    private val getCurrentPharmacist: GetCurrentPharmacistUseCase,
    private val getMyPharmacy: GetMyPharmacyUseCase,
) : ViewModel() {

    private val isReceivingOrdersFlow = MutableStateFlow(true)
    private val isLoadingFlow = MutableStateFlow(false)
    private val pharmacistFlow = MutableStateFlow<Pharmacist?>(null)
    private val pharmacyFlow = MutableStateFlow<MyPharmacy?>(null)
    private val errorFlow = MutableStateFlow<MedsyError?>(null)

    private val dataFlow = combine(
        isLoadingFlow,
        pharmacistFlow,
        pharmacyFlow,
        errorFlow
    ) { isLoading, pharmacist, pharmacy, error ->
        DataState(isLoading, pharmacist, pharmacy, error)
    }

    val state = combine(
        observePreferences(),
        isReceivingOrdersFlow,
        dataFlow
    ) { prefs, isReceiving, data ->
        ProfileState(
            themeMode = prefs.themeMode,
            isReceivingOrders = isReceiving,
            isLoading = data.isLoading,
            pharmacist = data.pharmacist,
            pharmacy = data.pharmacy,
            error = data.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProfileState(),
    )

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            isLoadingFlow.value = true
            errorFlow.value = null
            
            val pharmacistResult = getCurrentPharmacist()
            pharmacistResult.fold(
                onSuccess = { pharmacistFlow.value = it },
                onError = { errorFlow.value = it }
            )

            val pharmacyResult = getMyPharmacy()
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
            is ProfileUIIntent.LanguageChanged -> {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(intent.languageTag),
                )
            }
            is ProfileUIIntent.ReceivingStatusChanged -> {
                isReceivingOrdersFlow.value = intent.isReceiving
            }
            ProfileUIIntent.Logout -> viewModelScope.launch {
                clearSession()
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
                loadProfileData()
            }
        }
    }

    private data class DataState(
        val isLoading: Boolean,
        val pharmacist: Pharmacist?,
        val pharmacy: MyPharmacy?,
        val error: MedsyError?
    )
}
