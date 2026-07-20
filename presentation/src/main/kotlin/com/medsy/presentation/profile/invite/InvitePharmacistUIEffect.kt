package com.medsy.presentation.profile.invite

sealed interface InvitePharmacistUIEffect {
    data object NavigateBack : InvitePharmacistUIEffect
    data object NavigateToInvitationSent : InvitePharmacistUIEffect
}
