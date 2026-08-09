package com.medsy.presentation.auth.registerpharmacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.RegisterPharmacyUseCase
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.location.GetAddressFromCoordinatesUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.pharmacy.model.RegisterPharmacyParams
import com.medsy.presentation.R
import com.medsy.presentation.auth.registerpharmacy.components.MAX_LICENSE_PDF_BYTES
import com.medsy.presentation.auth.registerpharmacy.components.PDF_MEDIA_TYPE
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PharmacyRegistrationViewModel @Inject constructor(
    private val registerPharmacyUseCase: RegisterPharmacyUseCase,
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PharmacyRegistrationState())
    val state = _state.asStateFlow()

    private val _effect = Channel<PharmacyRegistrationUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var licenseBytes: ByteArray? = null

    fun onIntent(intent: PharmacyRegistrationUIIntent) {
        when (intent) {
            is PharmacyRegistrationUIIntent.PharmacyNameChanged -> _state.update {
                it.copy(pharmacyName = intent.value, pharmacyNameErrorRes = null)
            }

            is PharmacyRegistrationUIIntent.PhoneNumberChanged -> _state.update {
                it.copy(phoneNumber = intent.value)
            }

            is PharmacyRegistrationUIIntent.AddressChanged -> _state.update {
                it.copy(address = intent.value)
            }

            is PharmacyRegistrationUIIntent.LocationSelected -> {
                _state.update {
                    it.copy(
                        selectedLatitude = intent.latitude,
                        selectedLongitude = intent.longitude,
                        locationErrorRes = null,
                    )
                }
                updateReadableAddress(intent.latitude, intent.longitude)
            }

            is PharmacyRegistrationUIIntent.LicenseSelected -> handleLicenseSelected(intent)

            is PharmacyRegistrationUIIntent.LicenseSelectionFailed -> {
                licenseBytes = null
                _state.update {
                    it.copy(
                        licenseName = null,
                        licenseSizeKb = null,
                        licenseErrorRes = intent.messageRes,
                    )
                }
            }

            PharmacyRegistrationUIIntent.Submit -> submit()
        }
    }

    private fun updateReadableAddress(lat: Double, long: Double) {
        viewModelScope.launch {
            getAddressFromCoordinatesUseCase(lat, long)
                .onSuccess { result ->
                    result?.let { address ->
                        _state.update {
                            it.copy(address = address)
                        }
                    }
                }
        }
    }

    private fun handleLicenseSelected(intent: PharmacyRegistrationUIIntent.LicenseSelected) {
        val isPdf = intent.mimeType == PDF_MEDIA_TYPE ||
                intent.displayName.endsWith(".pdf", ignoreCase = true)
        val error = when {
            !isPdf -> R.string.pharmacy_registration_error_invalid_pdf
            intent.bytes.size > MAX_LICENSE_PDF_BYTES ->
                R.string.pharmacy_registration_error_pdf_too_large

            intent.bytes.isEmpty() -> R.string.pharmacy_registration_error_read_pdf
            else -> null
        }

        if (error != null) {
            licenseBytes = null
            _state.update {
                it.copy(
                    licenseName = null,
                    licenseSizeKb = null,
                    licenseErrorRes = error,
                )
            }
            return
        }

        licenseBytes = intent.bytes
        _state.update {
            it.copy(
                licenseName = intent.displayName,
                licenseSizeKb = intent.bytes.toKilobytes(),
                licenseErrorRes = null,
            )
        }
    }

    private fun submit() = viewModelScope.launch {
        val current = _state.value
        val latitude = current.selectedLatitude
        val longitude = current.selectedLongitude
        val license = licenseBytes

        val pharmacyNameError =
            if (current.pharmacyName.isBlank()) R.string.auth_error_required_field else null
        val locationError = when {
            latitude == null || longitude == null -> R.string.pharmacy_registration_error_location_required
            latitude !in -90.0..90.0 ||
                    longitude !in -180.0..180.0 ->
                R.string.pharmacy_registration_error_invalid_location

            else -> null
        }
        val licenseError = when {
            license == null -> R.string.pharmacy_registration_error_license_required
            license.isEmpty() || license.size > MAX_LICENSE_PDF_BYTES ->
                R.string.pharmacy_registration_error_pdf_too_large

            else -> null
        }

        if (
            pharmacyNameError != null ||
            locationError != null ||
            licenseError != null ||
            latitude == null ||
            longitude == null ||
            license == null
        ) {
            _state.update {
                it.copy(
                    pharmacyNameErrorRes = pharmacyNameError,
                    locationErrorRes = locationError,
                    licenseErrorRes = licenseError,
                )
            }
            return@launch
        }

        _state.update { it.copy(isSubmitting = true) }

        registerPharmacyUseCase(
            RegisterPharmacyParams(
                name = current.pharmacyName,
                latitude = latitude,
                longitude = longitude,
                address = current.address,
                phoneNumber = current.phoneNumber,
                licensePdfBytes = license,
            ),
        ).onSuccess {
            _state.update { it.copy(isSubmitting = false) }
            _effect.send(PharmacyRegistrationUIEffect.NavigatePendingApproval)
        }.onError { error ->
            _state.update { it.copy(isSubmitting = false) }
            _effect.send(
                PharmacyRegistrationUIEffect.ShowError(error.toRegistrationMessageRes()),
            )
        }
    }

    private fun MedsyError.toRegistrationMessageRes(): Int = when {
        this == MedsyError.Validation.INVALID_LOCATION ->
            R.string.pharmacy_registration_error_invalid_location

        this == MedsyError.Validation.INVALID_LICENSE_DOCUMENT ->
            R.string.pharmacy_registration_error_invalid_pdf

        this == MedsyError.Validation.PDF_TOO_LARGE ->
            R.string.pharmacy_registration_error_pdf_too_large

        this is MedsyError.Remote.Http && statusCode == 400 ->
            R.string.pharmacy_registration_error_submit_failed

        this is MedsyError.Remote.Http && statusCode == 413 ->
            R.string.pharmacy_registration_error_pdf_too_large

        else -> toMessageRes()
    }

    private fun ByteArray.toKilobytes(): Int = ((size + 1024 - 1) / 1024)
}
