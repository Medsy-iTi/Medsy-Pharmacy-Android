package com.medsy.presentation.profile

sealed interface ProfileUIEffect {
    data object OpenLogin : ProfileUIEffect
    data object OpenInvitePharmacist : ProfileUIEffect
    data object OpenPharmacistsList : ProfileUIEffect
    data object OpenPersonalInfo : ProfileUIEffect
}
