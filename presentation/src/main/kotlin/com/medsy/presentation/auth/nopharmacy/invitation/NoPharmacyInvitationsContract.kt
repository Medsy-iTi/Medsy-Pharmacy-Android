package com.medsy.presentation.auth.nopharmacy.invitation

import androidx.annotation.StringRes
import com.medsy.domain.invitation.model.PharmacyInvitation

data class NoPharmacyInvitationsState(
    val isLoading: Boolean = true,
    val invitations: List<PharmacyInvitation> = emptyList(),
    val acceptingInvitationId: Long? = null,
    @StringRes val errorRes: Int? = null,
)

sealed interface NoPharmacyInvitationsIntent {
    data object Retry : NoPharmacyInvitationsIntent
    data class AcceptInvitation(val invitationId: Long) : NoPharmacyInvitationsIntent
}

sealed interface NoPharmacyInvitationsEffect {
    data object NavigateHome : NoPharmacyInvitationsEffect
}
