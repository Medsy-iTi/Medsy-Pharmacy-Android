package com.medsy.presentation.profile.pharmacists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.medsy.domain.common.fold
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase

@HiltViewModel
class PharmacistsListViewModel @Inject constructor(
    private val getMyPharmacy: GetMyPharmacyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PharmacistsListState())
    val state = _state.asStateFlow()

    private val _effect = Channel<PharmacistsListUIEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadPharmacists()
    }

    private fun loadPharmacists() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = getMyPharmacy()
            result.fold(
                onSuccess = { pharmacy ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        pharmacyName = pharmacy.name,
                        pharmacists = pharmacy.pharmacists
                    )
                },
                onError = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error
                    )
                }
            )
        }
    }

    fun onIntent(intent: PharmacistsListUIIntent) {
        when (intent) {
            PharmacistsListUIIntent.NavigateBack -> {
                viewModelScope.launch { _effect.send(PharmacistsListUIEffect.NavigateBack) }
            }
            PharmacistsListUIIntent.InvitePharmacist -> {
                viewModelScope.launch { _effect.send(PharmacistsListUIEffect.NavigateToInvitePharmacist) }
            }
        }
    }
}
