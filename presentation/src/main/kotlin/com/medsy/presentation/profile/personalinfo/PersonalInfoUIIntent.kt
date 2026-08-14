package com.medsy.presentation.profile.personalinfo

sealed interface PersonalInfoUIIntent {
    data class PharmacyNameChanged(val name: String) : PersonalInfoUIIntent
    data class PharmacyAddressChanged(val address: String) : PersonalInfoUIIntent
    data class PharmacyPhoneChanged(val phone: String) : PersonalInfoUIIntent
    data class PharmacyLatitudeChanged(val latitude: String) : PersonalInfoUIIntent
    data class PharmacyLongitudeChanged(val longitude: String) : PersonalInfoUIIntent
    data object SaveProfile : PersonalInfoUIIntent
    data object ClearError : PersonalInfoUIIntent
}
