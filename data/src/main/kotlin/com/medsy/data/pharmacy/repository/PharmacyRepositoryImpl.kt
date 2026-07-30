package com.medsy.data.pharmacy.repository

import com.medsy.data.pharmacy.mapper.toDomain
import com.medsy.data.pharmacy.remote.api.PharmacyApi
import com.medsy.data.pharmacy.remote.dto.CreatePharmacyRequestDto
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.fold
import com.medsy.domain.common.map
import com.medsy.domain.pharmacy.model.MyPharmacy
import com.medsy.domain.pharmacy.model.RegisterPharmacyParams
import com.medsy.domain.pharmacy.repository.PharmacyRepository
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PharmacyRepositoryImpl @Inject constructor(
    private val api: PharmacyApi,
    moshi: Moshi,
) : PharmacyRepository {
    private val createPharmacyAdapter = moshi.adapter(CreatePharmacyRequestDto::class.java)

    private var cachedPharmacy: MyPharmacy? = null

    override suspend fun getMyPharmacy(forceRefresh: Boolean): MedsyResult<MyPharmacy, MedsyError> {
        if (!forceRefresh && cachedPharmacy != null) {
            return MedsyResult.Success(cachedPharmacy!!)
        }
        val result = safeApiCall { api.getMyPharmacy() }.fold(
            onSuccess = { MedsyResult.Success(it.toDomain()) },
            onError = { error: MedsyError ->
                if (error is MedsyError.Remote.Http && error.statusCode == HTTP_NOT_FOUND) {
                    MedsyResult.Error(MedsyError.Auth.NO_PHARMACY)
                } else {
                    MedsyResult.Error(error)
                }
            }
        )
        if (result is MedsyResult.Success) {
            cachedPharmacy = result.data
        }
        return result
    }

    override suspend fun registerPharmacy(
        params: RegisterPharmacyParams,
    ): MedsyResult<MyPharmacy, MedsyError.Remote> {
        val request = CreatePharmacyRequestDto(
            name = params.name.trim(),
            latitude = params.latitude,
            longitude = params.longitude,
            address = params.address?.trim()?.takeIf(String::isNotEmpty),
            phoneNumber = params.phoneNumber?.trim()?.takeIf(String::isNotEmpty),
        )
        val pharmacyRequestBody = createPharmacyAdapter.toJson(request)
            .toRequestBody(JSON_MEDIA_TYPE.toMediaType())
        val licenseBody = params.licensePdfBytes.toRequestBody(PDF_MEDIA_TYPE.toMediaType())
        val licensePart = MultipartBody.Part.createFormData(
            name = LICENSE_PART_NAME,
            filename = LICENSE_FILE_NAME,
            body = licenseBody,
        )

        return safeApiCall {
            api.createPharmacy(
                pharmacyRequest = pharmacyRequestBody,
                license = licensePart,
            )
        }.map { it.toDomain() }
    }

    override fun clearCache() {
        cachedPharmacy = null
    }

    private companion object {
        const val JSON_MEDIA_TYPE = "application/json"
        const val PDF_MEDIA_TYPE = "application/pdf"
        const val LICENSE_PART_NAME = "license"
        const val LICENSE_FILE_NAME = "license.pdf"
    }
}
