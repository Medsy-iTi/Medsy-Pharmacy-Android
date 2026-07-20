package com.medsy.presentation.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.model.RegisterPharmacyParams
import com.medsy.domain.auth.usecase.RegisterPharmacyUseCase
import com.medsy.domain.auth.usecase.RegisterUseCase
import com.medsy.domain.common.MedsyResult
import com.medsy.presentation.R
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
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val registerPharmacyUseCase: RegisterPharmacyUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUIState())
    val state = _state.asStateFlow()

    private val mutableEffect = Channel<RegisterUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    fun onIntent(intent: RegisterUIIntent) {
        Log.d("auth", "ViewModel: Intent received: $intent")
        when (intent) {
            is RegisterUIIntent.EmailChanged -> _state.update {
                it.copy(email = intent.value, emailErrorRes = null)
            }

            is RegisterUIIntent.PhoneChanged -> _state.update {
                it.copy(phoneNumber = intent.value, phoneErrorRes = null)
            }

            is RegisterUIIntent.FirstNameChanged -> _state.update {
                it.copy(firstName = intent.value, firstNameErrorRes = null)
            }

            is RegisterUIIntent.LastNameChanged -> _state.update {
                it.copy(lastName = intent.value, lastNameErrorRes = null)
            }

            is RegisterUIIntent.PasswordChanged -> _state.update {
                it.copy(password = intent.value, passwordErrorRes = null)
            }

            is RegisterUIIntent.DobChanged -> _state.update {
                it.copy(dob = intent.value)
            }

            is RegisterUIIntent.RoleChanged -> _state.update {
                it.copy(role = intent.value)
            }

            is RegisterUIIntent.AddressChanged -> _state.update {
                it.copy(homeAddress = intent.value)
            }

            is RegisterUIIntent.PharmacyIdChanged -> _state.update {
                it.copy(pharmacyId = intent.value)
            }

            is RegisterUIIntent.PharmacyNameChanged -> _state.update {
                it.copy(pharmacyName = intent.value, pharmacyNameErrorRes = null)
            }

            is RegisterUIIntent.PharmacyPhoneChanged -> _state.update {
                it.copy(pharmacyPhoneNumber = intent.value, pharmacyPhoneErrorRes = null)
            }

            is RegisterUIIntent.LicenseNumberChanged -> _state.update {
                it.copy(licenseNumber = intent.value, licenseNumberErrorRes = null)
            }

            is RegisterUIIntent.LocationPicked -> _state.update {
                it.copy(
                    latitude = intent.lat,
                    longitude = intent.lng,
                    pharmacyAddress = intent.address,
                    addressErrorRes = null
                )
            }

            is RegisterUIIntent.PharmacyAddressChanged -> _state.update {
                it.copy(pharmacyAddress = intent.value, addressErrorRes = null)
            }

            RegisterUIIntent.Submit -> submit()
            RegisterUIIntent.SubmitPharmacyInfo -> submitPharmacyInfo()
        }
    }

    private fun submit() = viewModelScope.launch {
        Log.d("auth", "ViewModel: Submitting registration")
        val s = _state.value

        val firstNameError = if (s.firstName.isBlank()) R.string.auth_error_required_field else null
        val lastNameError = if (s.lastName.isBlank()) R.string.auth_error_required_field else null
        val emailError = if (s.email.isBlank()) R.string.auth_error_required_field else null
        val phoneError = if (s.phoneNumber.isBlank()) R.string.auth_error_required_field else null
        val passwordError = when {
            s.password.isBlank() -> R.string.auth_error_required_field
            s.password.length < 6 -> R.string.auth_error_password_min_6
            else -> null
        }

        if (firstNameError != null || lastNameError != null || emailError != null ||
            phoneError != null || passwordError != null
        ) {
            Log.w("auth", "ViewModel: Validation failed")
            _state.update {
                it.copy(
                    firstNameErrorRes = firstNameError,
                    lastNameErrorRes = lastNameError,
                    emailErrorRes = emailError,
                    phoneErrorRes = phoneError,
                    passwordErrorRes = passwordError,
                )
            }
            return@launch
        }

        _state.update { it.copy(isLoading = true) }

        val params = RegisterParams(
            email = s.email,
            phoneNumber = s.phoneNumber,
            firstName = s.firstName,
            lastName = s.lastName,
            password = s.password,
            role = s.role,
            homeAddress = s.homeAddress,
            dob = s.dob,
            pharmacyId = s.pharmacyId
        )

        when (val result = registerUseCase(params)) {
            is MedsyResult.Success -> {
                Log.i("auth", "ViewModel: Registration success, navigating to OTP")
                _state.update { it.copy(isLoading = false) }
                mutableEffect.send(RegisterUIEffect.NavigateToOtp(s.email))
            }

            is MedsyResult.Error -> {
                Log.e("auth", "ViewModel: Registration error: ${result.error}")
                _state.update { it.copy(isLoading = false) }
                mutableEffect.send(RegisterUIEffect.ShowError(result.error.toMessageRes()))
            }
        }
    }

    private fun submitPharmacyInfo() = viewModelScope.launch {
        val s = _state.value
        val nameError = if (s.pharmacyName.isBlank()) R.string.auth_error_required_field else null
        val licenseError =
            if (s.licenseNumber.isBlank()) R.string.auth_error_required_field else null
        val phoneError =
            if (s.pharmacyPhoneNumber.isBlank()) R.string.auth_error_required_field else null
        val addressError =
            if (s.pharmacyAddress.isBlank()) R.string.auth_error_required_field else null

        if (nameError != null || licenseError != null || phoneError != null || addressError != null) {
            _state.update {
                it.copy(
                    pharmacyNameErrorRes = nameError,
                    licenseNumberErrorRes = licenseError,
                    pharmacyPhoneErrorRes = phoneError,
                    addressErrorRes = addressError
                )
            }
            return@launch
        }

        _state.update { it.copy(isLoading = true) }

        val params = RegisterPharmacyParams(
            name = s.pharmacyName,
            latitude = s.latitude ?: 0.0,
            longitude = s.longitude ?: 0.0,
            address = s.pharmacyAddress,
            phoneNumber = s.pharmacyPhoneNumber,
            license = s.licenseNumber
        )

        when (val result = registerPharmacyUseCase(params)) {
            is MedsyResult.Success -> {
                Log.i("auth", "ViewModel: Pharmacy registration success")
                _state.update { it.copy(isLoading = false) }
                mutableEffect.send(RegisterUIEffect.NavigateToDocuments)
            }

            is MedsyResult.Error -> {
                Log.e("auth", "ViewModel: Pharmacy registration error: ${result.error}")
                _state.update { it.copy(isLoading = false) }
                mutableEffect.send(RegisterUIEffect.ShowError(result.error.toMessageRes()))
            }
        }
    }
}
