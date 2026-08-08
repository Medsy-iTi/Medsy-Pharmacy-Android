package com.medsy.presentation.profile.personalinfo

sealed interface PersonalInfoUIIntent {
    data class FirstNameChanged(val name: String) : PersonalInfoUIIntent
    data class LastNameChanged(val name: String) : PersonalInfoUIIntent
    data class HomeAddressChanged(val address: String) : PersonalInfoUIIntent
    data class DobChanged(val dob: String) : PersonalInfoUIIntent
    data object SaveProfile : PersonalInfoUIIntent
    data object ClearError : PersonalInfoUIIntent
}
