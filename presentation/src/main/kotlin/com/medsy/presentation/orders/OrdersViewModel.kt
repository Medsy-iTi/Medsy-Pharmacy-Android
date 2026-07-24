package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderStatusConstants
import com.medsy.domain.orders.usecase.GetCurrentPharmacyRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getCurrentPharmacyRequestsUseCase: GetCurrentPharmacyRequestsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersUIState())
    val state = _state
        .onStart { loadOrders() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OrdersUIState(),
        )

    private val mutableEffect = Channel<OrdersUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    fun onIntent(intent: OrdersUIIntent) {
        when (intent) {
            is OrdersUIIntent.SearchQueryChanged -> _state.update {
                it.copy(searchQuery = intent.query)
            }

            is OrdersUIIntent.FilterSelected -> _state.update {
                it.copy(selectedFilter = intent.filter)
            }

            OrdersUIIntent.FilterIconClicked -> sendEffect(OrdersUIEffect.OpenFilters)

            is OrdersUIIntent.OrderClicked ->
                sendEffect(OrdersUIEffect.NavigateToOrderDetails(intent.orderId))

            is OrdersUIIntent.AcceptOrderClicked ->
                sendEffect(OrdersUIEffect.NavigateToOrderDetails(intent.orderId))

            is OrdersUIIntent.PrepareOrderClicked ->
                sendEffect(OrdersUIEffect.NavigateToOrderDetails(intent.orderId))
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = getCurrentPharmacyRequestsUseCase(page = 0, size = 10)

            result.onSuccess { orderPage ->
                val uiOrders = orderPage.content.map { it.toPresentation() }
                _state.update {
                    it.copy(
                        isLoading = false,
                        orders = uiOrders
                    )
                }
            }.onError {
                _state.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }
    private fun sendEffect(effect: OrdersUIEffect) {
        viewModelScope.launch {
            mutableEffect.send(effect)
        }
    }
}
fun OrderDetailsDomain.toPresentation(): OrderSummary {
    return OrderSummary(
        id = id,
        minutesAgo = createdAt,
        status = when (status) {
            OrderStatusConstants.PENDING, OrderStatusConstants.NEW -> OrderStatus.New
            OrderStatusConstants.IN_PROGRESS -> OrderStatus.InProgress
            else -> OrderStatus.New
        },
        customerName = "Customer #$customerId",
        customerPhone = "",
        customerAddress = deliveryAddress ?: "",
        total = 0,
        paymentMethod = PaymentMethod.Cash,
        paymentCardLastDigits = null,
    )
}