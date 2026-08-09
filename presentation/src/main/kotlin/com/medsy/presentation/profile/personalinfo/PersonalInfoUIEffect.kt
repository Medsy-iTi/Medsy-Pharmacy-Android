package com.medsy.presentation.profile.personalinfo

sealed interface PersonalInfoUIEffect {
    data object ProfileSaved : PersonalInfoUIEffect
}
