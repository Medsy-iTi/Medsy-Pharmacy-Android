package com.medsy.presentation.auth.nopharmacy

data class NoPharmacyState(
    val pendingInvitationCount: Int = 0,
)

sealed interface NoPharmacyIntent {
    data object RegisterPharmacy : NoPharmacyIntent
    data object SignOut : NoPharmacyIntent
    data object OpenInvitations : NoPharmacyIntent
}

sealed interface NoPharmacyEffect {
    data object NavigatePharmacyRegistration : NoPharmacyEffect
    data object NavigateLogin : NoPharmacyEffect
    data object NavigateInvitations : NoPharmacyEffect
}
