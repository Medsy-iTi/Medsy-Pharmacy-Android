package com.medsy.presentation.auth.nopharmacy.invitation

import androidx.annotation.StringRes
import com.medsy.domain.invitation.model.PharmacyInvitation

data class NoPharmacyInvitationsState(
    val isLoading: Boolean = true,
    val invitations: List<PharmacyInvitation> = emptyList(),
    val acceptingInvitationId: Long? = null,
    @StringRes val errorRes: Int? = null,
)
