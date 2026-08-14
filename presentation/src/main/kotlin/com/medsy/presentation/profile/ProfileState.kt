package com.medsy.presentation.profile

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.pharmacist.model.Pharmacist
import com.medsy.domain.pharmacy.model.MyPharmacy

data class ProfileState(
    val themeMode: ThemeMode = ThemeMode.System,
    val isReceivingOrders: Boolean = false,
    val isReceivingNotifications: Boolean = true,
    val isLoading: Boolean = false,
    val pharmacist: Pharmacist? = null,
    val pharmacy: MyPharmacy? = null,
    val error: MedsyError? = null,
    val isAvatarFemale: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isAvatarSheetOpen: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val isPresenceSwitchLoading: Boolean = false,
    val isNotificationSwitchLoading: Boolean = false,
)
