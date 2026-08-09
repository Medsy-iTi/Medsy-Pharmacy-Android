package com.medsy.presentation.auth.nopharmacy

sealed interface NoPharmacyUIIntent {
    data object RegisterPharmacy : NoPharmacyUIIntent
    data object SignOut : NoPharmacyUIIntent
    data object OpenInvitations : NoPharmacyUIIntent
}
