package com.medsy.presentation.orders.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.usecase.GetPharmacyOrderDetailsUseCase
import com.medsy.domain.orders.usecase.MarkOrderReadyUseCase
import com.medsy.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val getOrderDetails: GetPharmacyOrderDetailsUseCase,
    private val markOrderReady: MarkOrderReadyUseCase,
) : ViewModel() {
    val state = MutableStateFlow(OrderDetailsUIState())
    private val mutableEffect = Channel<OrderDetailsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()
    private var orderId: Long? = null
    private var loadJob: Job? = null
    private var readySubmissionInFlight = false

    fun onIntent(intent: OrderDetailsUIIntent) {
        when (intent) {
            is OrderDetailsUIIntent.Load -> if (orderId != intent.orderId) {
                orderId = intent.orderId
                load(intent.orderId, forceRefresh = false, userRefresh = false)
            }
            OrderDetailsUIIntent.Refresh -> orderId?.let { load(it, true, true) }
            OrderDetailsUIIntent.Retry -> orderId?.let { load(it, true, false) }
            OrderDetailsUIIntent.BackClicked -> sendEffect(OrderDetailsUIEffect.NavigateBack)
            OrderDetailsUIIntent.CallCustomerClicked -> state.value.order?.customerPhone
                ?.takeIf(String::isNotBlank)?.let { sendEffect(OrderDetailsUIEffect.DialPhoneNumber(it)) }
            OrderDetailsUIIntent.OpenLocationClicked -> state.value.order?.let { order ->
                val latitude = order.deliveryLatitude ?: return@let
                val longitude = order.deliveryLongitude ?: return@let
                sendEffect(OrderDetailsUIEffect.OpenLocation(latitude, longitude))
            }
            OrderDetailsUIIntent.MarkReadyClicked -> if (
                state.value.order?.status?.canMarkReady == true &&
                    !state.value.isMarkingReady && !state.value.isReadyActionBlocked
            ) state.update { it.copy(showReadyConfirmation = true) }
            OrderDetailsUIIntent.DismissReadyConfirmation -> state.update { it.copy(showReadyConfirmation = false) }
            OrderDetailsUIIntent.ConfirmMarkReady -> submitReady()
        }
    }

    private fun load(id: Long, forceRefresh: Boolean, userRefresh: Boolean) {
        if (userRefresh && state.value.isRefreshing) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (userRefresh) state.update { it.copy(isRefreshing = true) }
            else if (state.value.order == null) state.update { it.copy(isLoading = true, hasError = false) }
            getOrderDetails(id, forceRefresh).onSuccess { order ->
                state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        hasError = false,
                        order = order,
                        showReadyConfirmation = false,
                        isReadyActionBlocked = false,
                    )
                }
            }.onError {
                state.update { current ->
                    current.copy(
                        isLoading = false,
                        isRefreshing = false,
                        hasError = current.order == null,
                    )
                }
            }
        }
    }

    private fun submitReady() {
        val order = state.value.order ?: return
        if (!order.status.canMarkReady || readySubmissionInFlight || state.value.isReadyActionBlocked) return
        readySubmissionInFlight = true
        viewModelScope.launch {
            try {
                state.update { it.copy(showReadyConfirmation = false, isMarkingReady = true) }
                when (val result = markOrderReady(order.id)) {
                    is MedsyResult.Success -> {
                        state.update { it.copy(isReadyActionBlocked = true) }
                        if (reloadAuthoritativeOrder(order.id)) {
                            sendEffect(OrderDetailsUIEffect.ShowMessage(R.string.order_details_mark_ready_success))
                        }
                    }
                    is MedsyResult.Error -> {
                        val error = result.error
                        if (error is MedsyError.Remote.Http && error.statusCode == HTTP_BAD_REQUEST) {
                            state.update { it.copy(isReadyActionBlocked = true) }
                            reloadAuthoritativeOrder(order.id)
                        } else sendEffect(OrderDetailsUIEffect.ShowMessage(R.string.error_generic))
                    }
                }
            } finally {
                readySubmissionInFlight = false
                state.update { it.copy(isMarkingReady = false) }
            }
        }
    }

    private suspend fun reloadAuthoritativeOrder(orderId: Long): Boolean =
        when (val result = getOrderDetails(orderId, forceRefresh = true)) {
            is MedsyResult.Success -> {
                state.update {
                    it.copy(
                        order = result.data,
                        hasError = false,
                        showReadyConfirmation = false,
                        isReadyActionBlocked = false,
                    )
                }
                true
            }
            is MedsyResult.Error -> {
                sendEffect(OrderDetailsUIEffect.ShowMessage(R.string.order_details_refresh_failed))
                false
            }
        }

    private fun sendEffect(effect: OrderDetailsUIEffect) {
        viewModelScope.launch { mutableEffect.send(effect) }
    }

    private companion object { const val HTTP_BAD_REQUEST = 400 }
}
