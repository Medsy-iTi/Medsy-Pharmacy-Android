package com.medsy.presentation.profile

import com.medsy.domain.common.preferences.model.ThemeMode

import com.medsy.domain.common.MedsyError
import com.medsy.domain.pharmacist.model.Pharmacist
import com.medsy.domain.pharmacy.model.MyPharmacy

data class ProfileState(
    val themeMode: ThemeMode = ThemeMode.System,
    val isReceivingOrders: Boolean = true,
    val isLoading: Boolean = false,
    val pharmacist: Pharmacist? = null,
    val pharmacy: MyPharmacy? = null,
    val error: MedsyError? = null,
    val isAvatarFemale: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isAvatarSheetOpen: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val isPresenceSwitchLoading: Boolean = false
)

sealed interface ProfileUIIntent {
    data class ThemeChanged(val themeMode: ThemeMode) : ProfileUIIntent
    data class LanguageChanged(val languageTag: String) : ProfileUIIntent
    data class ReceivingStatusChanged(val isReceiving: Boolean) : ProfileUIIntent
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

sealed interface ProfileUIEffect {
    data object OpenLogin : ProfileUIEffect
    data object OpenInvitePharmacist : ProfileUIEffect
    data object OpenPharmacistsList : ProfileUIEffect
    data object OpenPersonalInfo : ProfileUIEffect

}
