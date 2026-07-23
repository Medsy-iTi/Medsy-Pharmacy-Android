package com.medsy.presentation.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.usecase.GetOrderDetailsUseCase
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.model.Order
import com.medsy.presentation.orderdetails.model.OrderMedicineItem
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
class OrderDetailsViewModel@Inject constructor(
    private val getOrderDetailsUseCase: GetOrderDetailsUseCase
    ) : ViewModel() {

    private var loadedOrderId: Long? = null

    private val _state = MutableStateFlow(OrderDetailsUIState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OrderDetailsUIState(),
        )

    private val mutableEffect = Channel<OrderDetailsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    fun onIntent(intent: OrderDetailsUIIntent) {
        when (intent) {
            is OrderDetailsUIIntent.LoadOrder -> {
                val idLong = intent.orderId
                if (loadedOrderId != idLong) {
                    loadedOrderId = idLong
                    loadOrder(idLong)
                }
            }

            OrderDetailsUIIntent.BackClicked -> sendEffect(OrderDetailsUIEffect.NavigateBack)

            OrderDetailsUIIntent.CallCustomerClicked -> {
                val phone = _state.value.order?.customerPhone ?: return
                sendEffect(OrderDetailsUIEffect.DialPhoneNumber(phone))
            }

            OrderDetailsUIIntent.OpenLocationClicked ->
                sendEffect(OrderDetailsUIEffect.OpenLocationOnMap)

            OrderDetailsUIIntent.ViewPaymentSummaryClicked ->
                sendEffect(OrderDetailsUIEffect.OpenPaymentSummary)

            OrderDetailsUIIntent.RejectOrderClicked -> rejectOrder()

            OrderDetailsUIIntent.ContactCustomerClicked ->
                sendEffect(OrderDetailsUIEffect.OpenCustomerChat)

            OrderDetailsUIIntent.AcceptOrderClicked -> acceptOrder()

            is OrderDetailsUIIntent.PharmacistNotesChanged -> {
                _state.update { it.copy(pharmacistNotes = intent.notes) }
            }
        }
    }

    private fun loadOrder(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getOrderDetailsUseCase(id)
                .onSuccess { domainOrder ->
                    if (domainOrder != null) {
                        val presentationOrder = domainOrder.toPresentation()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                order = presentationOrder
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

    private fun rejectOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            delay(300) // simulated backend call
            _state.update { it.copy(isSubmitting = false) }
            sendEffect(OrderDetailsUIEffect.ShowMessage(R.string.order_details_rejected_message))
            sendEffect(OrderDetailsUIEffect.NavigateBack)
        }
    }

    private fun acceptOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            delay(300) // simulated backend call
            _state.update { it.copy(isSubmitting = false) }
            sendEffect(OrderDetailsUIEffect.ShowMessage(R.string.order_details_accepted_message))
            sendEffect(OrderDetailsUIEffect.NavigateBack)
        }
    }

    private fun sendEffect(effect: OrderDetailsUIEffect) {
        viewModelScope.launch {
            mutableEffect.send(effect)
        }
    }
}
