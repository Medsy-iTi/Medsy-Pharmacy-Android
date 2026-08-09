package com.medsy.presentation.auth.nopharmacy.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.usecase.UpdateApprovalStatusUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.invitation.usecase.AcceptPharmacyInvitationUseCase
import com.medsy.domain.invitation.usecase.GetMyPendingInvitationsUseCase
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
class NoPharmacyInvitationsViewModel @Inject constructor(
    private val getMyPendingInvitationsUseCase: GetMyPendingInvitationsUseCase,
    private val acceptPharmacyInvitationUseCase: AcceptPharmacyInvitationUseCase,
    private val updateApprovalStatusUseCase: UpdateApprovalStatusUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NoPharmacyInvitationsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<NoPharmacyInvitationsUIEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadInvitations()
    }

    fun onIntent(intent: NoPharmacyInvitationsUIIntent) {
        when (intent) {
            NoPharmacyInvitationsUIIntent.Retry -> loadInvitations()
            is NoPharmacyInvitationsUIIntent.AcceptInvitation -> acceptInvitation(intent.invitationId)
        }
    }

    private fun loadInvitations() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, errorRes = null) }
        getMyPendingInvitationsUseCase()
            .onSuccess { invitations ->
                _state.update { it.copy(isLoading = false, invitations = invitations) }
            }
            .onError { error ->
                _state.update { it.copy(isLoading = false, errorRes = error.toMessageRes()) }
            }
    }

    private fun acceptInvitation(invitationId: Long) = viewModelScope.launch {
        if (_state.value.acceptingInvitationId != null) return@launch

        _state.update { it.copy(acceptingInvitationId = invitationId, errorRes = null) }
        acceptPharmacyInvitationUseCase(invitationId)
            .onSuccess {
                updateApprovalStatusUseCase(PharmacyApprovalStatus.Approved)
                _state.update { it.copy(acceptingInvitationId = null) }
                _effect.send(NoPharmacyInvitationsUIEffect.NavigateHome)
            }
            .onError { error ->
                _state.update {
                    it.copy(
                        acceptingInvitationId = null,
                        errorRes = error.toMessageRes(),
                    )
                }
            }
    }
}
