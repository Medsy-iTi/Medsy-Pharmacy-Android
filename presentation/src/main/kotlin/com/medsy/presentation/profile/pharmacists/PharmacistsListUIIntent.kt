package com.medsy.presentation.profile.pharmacists

sealed interface PharmacistsListUIIntent {
    data object NavigateBack : PharmacistsListUIIntent
    data object InvitePharmacist : PharmacistsListUIIntent
}
