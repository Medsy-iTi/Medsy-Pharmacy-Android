package com.medsy.presentation.profile.invite

sealed interface InvitePharmacistUIIntent {
    data class EmailChanged(val email: String) : InvitePharmacistUIIntent
    data object Submit : InvitePharmacistUIIntent
    data object NavigateBack : InvitePharmacistUIIntent
}
