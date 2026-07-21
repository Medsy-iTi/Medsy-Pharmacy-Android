package com.medsy.presentation.profile.personalinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.pharmacist.usecase.GetCurrentPharmacistUseCase
import com.medsy.domain.pharmacist.usecase.UpdateCurrentPharmacistUseCase
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
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
    private val getCurrentPharmacist: GetCurrentPharmacistUseCase,
    private val getMyPharmacy: GetMyPharmacyUseCase,
    private val updateCurrentPharmacist: UpdateCurrentPharmacistUseCase
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
            
            val pharmacistDeferred = async { getCurrentPharmacist() }
            val pharmacyDeferred = async { getMyPharmacy() }
            
            val pharmacistResult = pharmacistDeferred.await()
            val pharmacyResult = pharmacyDeferred.await()
            loadingJob.cancel()
            
            var hasError = false
            
            when (pharmacistResult) {
                is MedsyResult.Success -> {
                    _state.update {
                        it.copy(
                            pharmacist = pharmacistResult.data,
                            firstName = pharmacistResult.data.firstName,
                            lastName = pharmacistResult.data.lastName,
                            homeAddress = pharmacistResult.data.homeAddress ?: "",
                            dob = pharmacistResult.data.dob ?: ""
                        )
                    }
                }
                is MedsyResult.Error -> {
                    hasError = true
                    _state.update { it.copy(error = pharmacistResult.error) }
                }
            }
            
            if (!hasError) {
                when (pharmacyResult) {
                    is MedsyResult.Success -> {
                        _state.update { it.copy(pharmacy = pharmacyResult.data) }
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
            is PersonalInfoUIIntent.FirstNameChanged -> {
                _state.update { it.copy(firstName = intent.name) }
            }
            is PersonalInfoUIIntent.LastNameChanged -> {
                _state.update { it.copy(lastName = intent.name) }
            }
            is PersonalInfoUIIntent.HomeAddressChanged -> {
                _state.update { it.copy(homeAddress = intent.address) }
            }
            is PersonalInfoUIIntent.DobChanged -> {
                _state.update { it.copy(dob = intent.dob) }
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
            
            val result = updateCurrentPharmacist(
                firstName = currentState.firstName.takeIf { it.isNotBlank() },
                lastName = currentState.lastName.takeIf { it.isNotBlank() },
                homeAddress = currentState.homeAddress.takeIf { it.isNotBlank() },
                dob = currentState.dob.takeIf { it.isNotBlank() }
            )
            
            when (result) {
                is MedsyResult.Success -> {
                    _state.update { 
                        it.copy(
                            isSaving = false,
                            pharmacist = result.data
                        )
                    }
                    mutableEffect.send(PersonalInfoUIEffect.ProfileSaved)
                }
                is MedsyResult.Error -> {
                    _state.update { it.copy(isSaving = false, saveError = result.error) }
                }
            }
        }
    }
}
