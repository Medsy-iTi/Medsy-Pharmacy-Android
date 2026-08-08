package com.medsy.presentation.auth.nopharmacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.ClearSessionUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.invitation.usecase.GetMyPendingInvitationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoPharmacyViewModel @Inject constructor(
    private val clearSessionUseCase: ClearSessionUseCase,
    private val getMyPendingInvitationsUseCase: GetMyPendingInvitationsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NoPharmacyState())
    val state = _state.asStateFlow()

    private val _effect = Channel<NoPharmacyUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadInvitations()
    }

    fun onIntent(intent: NoPharmacyUIIntent) {
        when (intent) {
            NoPharmacyUIIntent.RegisterPharmacy -> viewModelScope.launch {
                _effect.send(NoPharmacyUIEffect.NavigatePharmacyRegistration)
            }

            NoPharmacyUIIntent.SignOut -> viewModelScope.launch {
                clearSessionUseCase()
                _effect.send(NoPharmacyUIEffect.NavigateLogin)
            }

            NoPharmacyUIIntent.OpenInvitations -> viewModelScope.launch {
                _effect.send(NoPharmacyUIEffect.NavigateInvitations)
            }

        }
    }

    private fun loadInvitations() = viewModelScope.launch {
        getMyPendingInvitationsUseCase()
            .onSuccess { invitations ->
                _state.update { it.copy(pendingInvitationCount = invitations.size) }
            }
            .onError {
                _state.update { it.copy(pendingInvitationCount = 0) }
            }
    }
}
