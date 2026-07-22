package com.medsy.presentation.profile.pharmacists

import com.medsy.domain.common.MedsyError
import com.medsy.domain.pharmacy.model.PharmacyPharmacist

data class PharmacistsListState(
    val isLoading: Boolean = false,
    val pharmacyName: String = "",
    val pharmacists: List<PharmacyPharmacist> = emptyList(),
    val error: MedsyError? = null
) {
    val pharmacistsCount: Int
        get() = pharmacists.size
}
