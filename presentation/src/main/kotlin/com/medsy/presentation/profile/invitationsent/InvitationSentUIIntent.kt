package com.medsy.presentation.profile.invitationsent

sealed interface InvitationSentUIIntent {
    data object NavigateBack : InvitationSentUIIntent
    data object InviteAnother : InvitationSentUIIntent
    data object ReturnToProfile : InvitationSentUIIntent
}
