package com.medsy.presentation.profile.invitationsent

sealed interface InvitationSentUIEffect {
    data object NavigateBack : InvitationSentUIEffect
    data object ReturnToProfile : InvitationSentUIEffect
}
