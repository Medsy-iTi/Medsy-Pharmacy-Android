package com.medsy.presentation.auth.nopharmacy.invitation

sealed interface NoPharmacyInvitationsUIIntent {
    data object Retry : NoPharmacyInvitationsUIIntent
    data class AcceptInvitation(val invitationId: Long) : NoPharmacyInvitationsUIIntent
}
