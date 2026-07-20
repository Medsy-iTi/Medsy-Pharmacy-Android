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

@HiltViewModel
class PharmacistsListViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(PharmacistsListState())
    val state = _state.asStateFlow()

    private val _effect = Channel<PharmacistsListUIEffect>()
    val effect = _effect.receiveAsFlow()

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
