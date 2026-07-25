package com.medsy.presentation.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.usecase.GetRequestDetailsUseCase
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.mapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RequestDetailsViewModel @Inject constructor(
    private val getRequestDetailsUseCase: GetRequestDetailsUseCase
) : ViewModel() {

    private var loadedRequestId: Long? = null

    private val _state = MutableStateFlow(RequestDetailsUIState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RequestDetailsUIState(),
        )

    private val mutableEffect = Channel<RequestDetailsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    fun onIntent(intent: RequestDetailsUIIntent) {
        when (intent) {
            is RequestDetailsUIIntent.LoadRequest -> {
                val idLong = intent.requestId
                if (loadedRequestId != idLong) {
                    loadedRequestId = idLong
                    loadRequest(idLong)
                }
            }

            RequestDetailsUIIntent.BackClicked -> sendEffect(RequestDetailsUIEffect.NavigateBack)

            RequestDetailsUIIntent.CallCustomerClicked -> {
                val phone = _state.value.request?.customerPhone ?: return
                sendEffect(RequestDetailsUIEffect.DialPhoneNumber(phone))
            }

            RequestDetailsUIIntent.OpenLocationClicked ->
                sendEffect(RequestDetailsUIEffect.OpenLocationOnMap)

            RequestDetailsUIIntent.ViewPaymentSummaryClicked ->
                sendEffect(RequestDetailsUIEffect.OpenPaymentSummary)

            RequestDetailsUIIntent.RejectRequestClicked -> rejectRequest()

            RequestDetailsUIIntent.ContactCustomerClicked ->
                sendEffect(RequestDetailsUIEffect.OpenCustomerChat)

            RequestDetailsUIIntent.AcceptRequestClicked -> acceptRequest()

            is RequestDetailsUIIntent.PharmacistNotesChanged -> {
                _state.update { it.copy(pharmacistNotes = intent.notes) }
            }
        }
    }

    private fun loadRequest(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getRequestDetailsUseCase(id)
                .onSuccess { domainRequest ->
                    if (domainRequest != null) {
                        val presentationRequest = domainRequest.toPresentation()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                request = presentationRequest
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                            )
                        }
                    }
                }
                .onError {
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
        }
    }

    private fun rejectRequest() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            delay(300) // simulated backend call
            _state.update { it.copy(isSubmitting = false) }
            sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.request_details_rejected_message))
            sendEffect(RequestDetailsUIEffect.NavigateBack)
        }
    }

    private fun acceptRequest() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            delay(300) // simulated backend call
            _state.update { it.copy(isSubmitting = false) }
            sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.request_details_accepted_message))
            sendEffect(RequestDetailsUIEffect.NavigateBack)
        }
    }

    private fun sendEffect(effect: RequestDetailsUIEffect) {
        viewModelScope.launch {
            mutableEffect.send(effect)
        }
    }
}
