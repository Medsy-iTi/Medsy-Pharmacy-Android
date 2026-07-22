package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.orders.usecase.GetOrdersUseCase
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
import com.medsy.domain.orders.model.OrderStatus as DomainOrderStatus
import com.medsy.domain.orders.model.PaymentMethod as DomainPaymentMethod
import com.medsy.domain.orders.model.OrderSummary as DomainOrderSummary

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
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

            getOrdersUseCase()
                .catch { _ ->
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
                .collect { domainOrders ->
                    val uiOrders = domainOrders.map { it.toPresentation() }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            orders = uiOrders
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

fun DomainOrderSummary.toPresentation(): OrderSummary {
    return OrderSummary(
        id = id,
        minutesAgo = minutesAgo,
        status = when (status) {
            DomainOrderStatus.New -> OrderStatus.New
            DomainOrderStatus.InProgress -> OrderStatus.InProgress
            DomainOrderStatus.Delivered -> OrderStatus.Delivered
            else -> OrderStatus.New
        },
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        total = total.toInt(),
        paymentMethod = when (paymentMethod) {
            DomainPaymentMethod.Cash -> PaymentMethod.Cash
            DomainPaymentMethod.Visa -> PaymentMethod.Visa
            else -> PaymentMethod.Cash
        },
        paymentCardLastDigits = paymentCardLastDigits,
    )
}