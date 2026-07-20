package com.medsy.presentation.auth.nopharmacy

sealed interface NoPharmacyIntent {
    data object RegisterPharmacy : NoPharmacyIntent
    data object SignOut : NoPharmacyIntent
}

sealed interface NoPharmacyEffect {
    data object NavigatePharmacyRegistration : NoPharmacyEffect
    data object NavigateLogin : NoPharmacyEffect
}
