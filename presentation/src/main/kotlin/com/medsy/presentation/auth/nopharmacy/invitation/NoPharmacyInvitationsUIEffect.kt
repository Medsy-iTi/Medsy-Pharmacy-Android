package com.medsy.presentation.auth.nopharmacy.invitation

sealed interface NoPharmacyInvitationsUIEffect {
    data object NavigateHome : NoPharmacyInvitationsUIEffect
}
