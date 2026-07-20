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

import com.medsy.domain.common.fold
import com.medsy.domain.common.MedsyError
import com.medsy.domain.invitation.usecase.InvitePharmacistUseCase
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase

@HiltViewModel
class InvitePharmacistViewModel @Inject constructor(
    private val getMyPharmacy: GetMyPharmacyUseCase,
    private val invitePharmacist: InvitePharmacistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(InvitePharmacistState())
    val state = _state.asStateFlow()

    private val _effect = Channel<InvitePharmacistUIEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: InvitePharmacistUIIntent) {
        when (intent) {
            is InvitePharmacistUIIntent.EmailChanged -> {
                _state.update { it.copy(email = intent.email, error = null) }
            }
            InvitePharmacistUIIntent.NavigateBack -> {
                viewModelScope.launch { _effect.send(InvitePharmacistUIEffect.NavigateBack) }
            }
            InvitePharmacistUIIntent.Submit -> {
                submitInvitation()
            }
        }
    }

    private fun submitInvitation() {
        val email = _state.value.email.trim()
        if (email.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            // First get the pharmacy ID
            val pharmacyResult = getMyPharmacy()
            var pharmacyId: Long? = null
            
            pharmacyResult.fold(
                onSuccess = { pharmacyId = it.id },
                onError = {
                    _state.update { state -> state.copy(isLoading = false, error = "Could not find your pharmacy") }
                }
            )

            if (pharmacyId == null) return@launch

            // Then invite the pharmacist
            val inviteResult = invitePharmacist(pharmacyId!!, email)
            inviteResult.fold(
                onSuccess = {
                    _state.update { state -> state.copy(isLoading = false) }
                    _effect.send(InvitePharmacistUIEffect.NavigateToInvitationSent)
                },
                onError = { error ->
                    val errorMessage = if (error is MedsyError.Remote.Http) error.serverMessage else "Failed to send invitation"
                    _state.update { state -> state.copy(isLoading = false, error = errorMessage) }
                }
            )
        }
    }
}
