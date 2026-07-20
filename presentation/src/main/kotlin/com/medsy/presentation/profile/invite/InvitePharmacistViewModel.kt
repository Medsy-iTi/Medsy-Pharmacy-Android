package com.medsy.presentation.profile.invite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitePharmacistViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(InvitePharmacistState())
    val state = _state.asStateFlow()

    private val _effect = Channel<InvitePharmacistUIEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: InvitePharmacistUIIntent) {
        when (intent) {
            is InvitePharmacistUIIntent.EmailChanged -> {
                _state.update { it.copy(email = intent.email) }
            }
            InvitePharmacistUIIntent.NavigateBack -> {
                viewModelScope.launch { _effect.send(InvitePharmacistUIEffect.NavigateBack) }
            }
            InvitePharmacistUIIntent.Submit -> {
                // Static flow: navigate to InvitationSent directly
                viewModelScope.launch { _effect.send(InvitePharmacistUIEffect.NavigateToInvitationSent) }
            }
        }
    }
}
