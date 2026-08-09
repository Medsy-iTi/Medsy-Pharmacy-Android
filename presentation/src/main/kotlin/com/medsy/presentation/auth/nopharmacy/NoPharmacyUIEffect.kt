package com.medsy.presentation.auth.nopharmacy

sealed interface NoPharmacyUIEffect {
    data object NavigatePharmacyRegistration : NoPharmacyUIEffect
    data object NavigateLogin : NoPharmacyUIEffect
    data object NavigateInvitations : NoPharmacyUIEffect
}
