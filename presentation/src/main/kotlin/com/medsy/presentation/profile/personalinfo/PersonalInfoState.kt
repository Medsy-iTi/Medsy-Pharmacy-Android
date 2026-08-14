package com.medsy.presentation.profile.personalinfo

import com.medsy.domain.common.MedsyError
import com.medsy.domain.pharmacist.model.Pharmacist
import com.medsy.domain.pharmacy.model.MyPharmacy

data class PersonalInfoState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val pharmacy: MyPharmacy? = null,
    val error: MedsyError? = null,
    val saveError: MedsyError? = null,
    val pharmacyName: String = "",
    val pharmacyAddress: String = "",
    val pharmacyPhoneNumber: String = "",
    val pharmacyLatitude: String = "",
    val pharmacyLongitude: String = "",
)
