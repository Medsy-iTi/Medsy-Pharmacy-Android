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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observePreferences: ObserveUserPreferencesUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    private val clearSession: ClearSessionUseCase,
) : ViewModel() {
    val state = observePreferences()
        .map { ProfileState(themeMode = it.themeMode) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState(),
        )

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
            ProfileUIIntent.Logout -> viewModelScope.launch {
                clearSession()
                mutableEffect.send(ProfileUIEffect.OpenLogin)
            }
        }
    }
}
