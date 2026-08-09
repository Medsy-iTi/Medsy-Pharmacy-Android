package com.medsy.presentation.profile.personalinfo

import com.medsy.domain.common.MedsyError
import com.medsy.domain.pharmacist.model.Pharmacist
import com.medsy.domain.pharmacy.model.MyPharmacy

data class PersonalInfoState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val pharmacist: Pharmacist? = null,
    val pharmacy: MyPharmacy? = null,
    val error: MedsyError? = null,
    val saveError: MedsyError? = null,
    val firstName: String = "",
    val lastName: String = "",
    val homeAddress: String = "",
    val dob: String = "",
)
