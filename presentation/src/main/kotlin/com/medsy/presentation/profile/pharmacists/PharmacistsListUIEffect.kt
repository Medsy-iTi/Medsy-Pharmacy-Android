package com.medsy.presentation.profile.pharmacists

sealed interface PharmacistsListUIEffect {
    data object NavigateBack : PharmacistsListUIEffect
    data object NavigateToInvitePharmacist : PharmacistsListUIEffect
}
