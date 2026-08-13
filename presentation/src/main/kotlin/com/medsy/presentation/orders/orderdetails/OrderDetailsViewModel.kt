package com.medsy.presentation.orders.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.usecase.GetPharmacyOrderDetailsUseCase
import com.medsy.domain.orders.usecase.MarkOrderDeliveredUseCase
import com.medsy.domain.orders.usecase.MarkOrderOutForDeliveryUseCase
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
    private val markOrderOutForDelivery: MarkOrderOutForDeliveryUseCase,
    private val markOrderDelivered: MarkOrderDeliveredUseCase,
) : ViewModel() {
    val state = MutableStateFlow(OrderDetailsUIState())
    private val mutableEffect = Channel<OrderDetailsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()
    private var orderId: Long? = null
    private var loadJob: Job? = null
    private var statusSubmissionInFlight = false

    fun onIntent(intent: OrderDetailsUIIntent) {
        when (intent) {
            is OrderDetailsUIIntent.Load -> if (orderId != intent.orderId) {
                orderId = intent.orderId
                load(intent.orderId, forceRefresh = false, userRefresh = false)
            }

            OrderDetailsUIIntent.Refresh -> orderId?.let { load(it, true, userRefresh = true) }

            OrderDetailsUIIntent.Retry -> orderId?.let { load(it, true, userRefresh = false) }

            OrderDetailsUIIntent.BackClicked -> sendEffect(OrderDetailsUIEffect.NavigateBack)

            OrderDetailsUIIntent.CallCustomerClicked -> state.value.order?.customerPhone
                ?.takeIf(String::isNotBlank)
                ?.let { sendEffect(OrderDetailsUIEffect.DialPhoneNumber(it)) }

            OrderDetailsUIIntent.OpenLocationClicked -> state.value.order?.let { order ->
                val latitude = order.deliveryLatitude ?: return@let
                val longitude = order.deliveryLongitude ?: return@let
                sendEffect(OrderDetailsUIEffect.OpenLocation(latitude, longitude))
            }

            OrderDetailsUIIntent.StatusActionClicked -> if (
                state.value.order?.statusTransition() != null &&
                !state.value.isUpdatingStatus && !state.value.isStatusActionBlocked
            ) state.update { it.copy(showStatusConfirmation = true) }

            OrderDetailsUIIntent.DismissStatusConfirmation ->
                state.update { it.copy(showStatusConfirmation = false) }

            OrderDetailsUIIntent.ConfirmStatusAction -> submitStatusAction()
        }
    }

    private fun load(id: Long, forceRefresh: Boolean, userRefresh: Boolean) {
        if (userRefresh && state.value.isRefreshing) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (userRefresh) state.update { it.copy(isRefreshing = true) }
            else if (state.value.order == null) state.update {
                it.copy(
                    isLoading = true,
                    hasError = false
                )
            }
            getOrderDetails(id, forceRefresh).onSuccess { order ->
                state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        hasError = false,
                        order = order,
                        showStatusConfirmation = false,
                        isStatusActionBlocked = false,
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

    private fun submitStatusAction() {
        val order = state.value.order ?: return
        val transition = order.statusTransition() ?: return
        if (statusSubmissionInFlight || state.value.isStatusActionBlocked) return
        statusSubmissionInFlight = true
        viewModelScope.launch {
            try {
                state.update { it.copy(showStatusConfirmation = false, isUpdatingStatus = true) }
                val result = when (transition) {
                    OrderStatusTransition.MarkReady -> markOrderReady(order.id)
                    OrderStatusTransition.StartDelivery -> markOrderOutForDelivery(order.id)
                    OrderStatusTransition.MarkCollected,
                    OrderStatusTransition.MarkDelivered -> markOrderDelivered(order.id)
                }
                result
                    .onSuccess {
                        state.update { it.copy(isStatusActionBlocked = true) }
                        if (reloadAuthoritativeOrder(order.id)) {
                            sendEffect(OrderDetailsUIEffect.ShowMessage(R.string.order_details_status_update_success))
                        }
                    }
                    .onError { error ->
                        val error = error
                        if (error is MedsyError.Remote.Http) {
                            state.update { it.copy(isStatusActionBlocked = true) }
                            reloadAuthoritativeOrder(order.id)
                        } else sendEffect(OrderDetailsUIEffect.ShowMessage(R.string.error_generic))

                    }

            } finally {
                statusSubmissionInFlight = false
                state.update { it.copy(isUpdatingStatus = false) }
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
                        showStatusConfirmation = false,
                        isStatusActionBlocked = false,
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

}
