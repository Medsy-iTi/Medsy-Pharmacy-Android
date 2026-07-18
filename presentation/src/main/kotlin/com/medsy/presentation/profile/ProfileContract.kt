package com.medsy.presentation.profile

import com.medsy.domain.common.preferences.model.ThemeMode

data class ProfileState(
    val themeMode: ThemeMode = ThemeMode.System,
)

sealed interface ProfileUIIntent {
    data class ThemeChanged(val themeMode: ThemeMode) : ProfileUIIntent
    data class LanguageChanged(val languageTag: String) : ProfileUIIntent
    data object Logout : ProfileUIIntent
}

sealed interface ProfileUIEffect {
    data object OpenLogin : ProfileUIEffect
}
