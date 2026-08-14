package com.medsy.presentation.profile

import com.medsy.domain.common.preferences.model.ThemeMode

sealed interface ProfileUIIntent {
    data class ThemeChanged(val themeMode: ThemeMode) : ProfileUIIntent
    data class LanguageChanged(val languageTag: String) : ProfileUIIntent
    data class ReceivingStatusChanged(val isReceiving: Boolean) : ProfileUIIntent
    data class ReceivingNotificationsChanged(val isReceiving: Boolean) : ProfileUIIntent
    data class ToggleAvatarGender(val isFemale: Boolean) : ProfileUIIntent
    data object OpenAvatarSheet : ProfileUIIntent
    data object CloseAvatarSheet : ProfileUIIntent
    data object ShowLogoutDialog : ProfileUIIntent
    data object HideLogoutDialog : ProfileUIIntent
    data object Logout : ProfileUIIntent
    data object NavigateToInvitePharmacist : ProfileUIIntent
    data object NavigateToPharmacistsList : ProfileUIIntent
    data object NavigateToPersonalInfo : ProfileUIIntent
    data object Refresh : ProfileUIIntent
}
