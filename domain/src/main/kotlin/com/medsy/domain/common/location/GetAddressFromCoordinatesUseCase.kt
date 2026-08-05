package com.medsy.domain.common.location

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class GetAddressFromCoordinatesUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
    ): MedsyResult<String?, MedsyError.Local> =
        geocodingRepository.getAddress(latitude, longitude)
}