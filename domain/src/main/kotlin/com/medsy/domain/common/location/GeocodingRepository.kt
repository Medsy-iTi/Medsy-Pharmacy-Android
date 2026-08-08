package com.medsy.domain.common.location

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface GeocodingRepository {

    suspend fun getAddress(
        latitude: Double,
        longitude: Double,
    ): MedsyResult<String?, MedsyError.Local>

}