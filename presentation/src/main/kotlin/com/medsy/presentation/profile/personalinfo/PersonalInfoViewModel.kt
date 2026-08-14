package com.medsy.presentation.profile.personalinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import com.medsy.domain.pharmacy.usecase.UpdatePharmacyUseCase
import com.medsy.domain.pharmacy.model.UpdatePharmacyParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val getMyPharmacy: GetMyPharmacyUseCase,
    private val updatePharmacyUseCase: UpdatePharmacyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PersonalInfoState())
    val state = _state.asStateFlow()

    private val mutableEffect = Channel<PersonalInfoUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val loadingJob = launch {
                kotlinx.coroutines.delay(100)
                _state.update { it.copy(isLoading = true, error = null) }
            }
            
            val pharmacyResult = async { getMyPharmacy() }.await()
            loadingJob.cancel()
            
            var hasError = false
            
            if (!hasError) {
                when (pharmacyResult) {
                    is MedsyResult.Success -> {
                        val p = pharmacyResult.data
                        _state.update { 
                            it.copy(
                                pharmacy = p,
                                pharmacyName = p.name,
                                pharmacyAddress = p.address ?: "",
                                pharmacyPhoneNumber = p.phoneNumber ?: "",
                                pharmacyLatitude = p.latitude.toString(),
                                pharmacyLongitude = p.longitude.toString()
                            ) 
                        }
                    }
                    is MedsyResult.Error -> {
                        _state.update { it.copy(error = pharmacyResult.error) }
                    }
                }
            }
            
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onIntent(intent: PersonalInfoUIIntent) {
        when (intent) {
            is PersonalInfoUIIntent.PharmacyNameChanged -> {
                _state.update { it.copy(pharmacyName = intent.name) }
            }
            is PersonalInfoUIIntent.PharmacyAddressChanged -> {
                _state.update { it.copy(pharmacyAddress = intent.address) }
            }
            is PersonalInfoUIIntent.PharmacyPhoneChanged -> {
                _state.update { it.copy(pharmacyPhoneNumber = intent.phone) }
            }
            is PersonalInfoUIIntent.PharmacyLatitudeChanged -> {
                _state.update { it.copy(pharmacyLatitude = intent.latitude) }
            }
            is PersonalInfoUIIntent.PharmacyLongitudeChanged -> {
                _state.update { it.copy(pharmacyLongitude = intent.longitude) }
            }
            PersonalInfoUIIntent.SaveProfile -> saveProfile()
            PersonalInfoUIIntent.ClearError -> {
                _state.update { it.copy(error = null, saveError = null) }
            }
        }
    }

    private fun saveProfile() {
        val currentState = _state.value
        if (currentState.isSaving) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, saveError = null) }
            
            if (currentState.pharmacy?.isAdmin == true) {
                val pharmacyResult = updatePharmacyUseCase(
                    UpdatePharmacyParams(
                        pharmacyId = currentState.pharmacy.id,
                        name = currentState.pharmacyName.takeIf { it.isNotBlank() },
                        latitude = currentState.pharmacyLatitude.toDoubleOrNull(),
                        longitude = currentState.pharmacyLongitude.toDoubleOrNull(),
                        address = currentState.pharmacyAddress.takeIf { it.isNotBlank() },
                        phoneNumber = currentState.pharmacyPhoneNumber.takeIf { it.isNotBlank() }
                    )
                )
                when (pharmacyResult) {
                    is MedsyResult.Success -> {
                        _state.update { 
                            it.copy(isSaving = false, pharmacy = pharmacyResult.data) 
                        }
                        mutableEffect.send(PersonalInfoUIEffect.ProfileSaved)
                    }
                    is MedsyResult.Error -> {
                        _state.update { it.copy(isSaving = false, saveError = pharmacyResult.error) }
                    }
                }
            } else {
                _state.update { it.copy(isSaving = false) }
                mutableEffect.send(PersonalInfoUIEffect.ProfileSaved)
            }
        }
    }
}
