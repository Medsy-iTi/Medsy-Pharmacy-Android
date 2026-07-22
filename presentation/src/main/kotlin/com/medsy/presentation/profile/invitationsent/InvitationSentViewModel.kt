package com.medsy.presentation.profile.invitationsent

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
class InvitationSentViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(InvitationSentState())
    val state = _state.asStateFlow()

    private val _effect = Channel<InvitationSentUIEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: InvitationSentUIIntent) {
        when (intent) {
            InvitationSentUIIntent.NavigateBack,
            InvitationSentUIIntent.InviteAnother -> {
                viewModelScope.launch { _effect.send(InvitationSentUIEffect.NavigateBack) }
            }
            InvitationSentUIIntent.ReturnToProfile -> {
                viewModelScope.launch { _effect.send(InvitationSentUIEffect.ReturnToProfile) }
            }
        }
    }
}
