package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OrdersViewModel @Inject constructor() : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(OrdersUIState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadOrders()
                hasLoadedInitialData = true
            }
        }
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

            // Accept/Prepare open the order for now — the full action lives on
            // Order Details, which already has Accept/Reject wired to the backend.
            is OrdersUIIntent.AcceptOrderClicked ->
                sendEffect(OrdersUIEffect.NavigateToOrderDetails(intent.orderId))

            is OrdersUIIntent.PrepareOrderClicked ->
                sendEffect(OrdersUIEffect.NavigateToOrderDetails(intent.orderId))
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(300) // simulated repository call

            val orders = listOf(
                OrderSummary(
                    id = "1258",
                    minutesAgo = 5,
                    status = OrderStatus.New,
                    customerName = "Omar Ramadan",
                    customerPhone = "011 522 671 25",
                    customerAddress = "Nile St, Maadi, Cairo",
                    total = 165,
                    paymentMethod = PaymentMethod.Cash,
                ),
                OrderSummary(
                    id = "1257",
                    minutesAgo = 15,
                    status = OrderStatus.InProgress,
                    customerName = "Mennatallah Mahmoud",
                    customerPhone = "010 9876 5432",
                    customerAddress = "Nile St, Maadi",
                    total = 230,
                    paymentMethod = PaymentMethod.Visa,
                    paymentCardLastDigits = "3456",
                ),
            )

            _state.update { it.copy(isLoading = false, orders = orders) }
        }
    }

    private fun sendEffect(effect: OrdersUIEffect) {
        viewModelScope.launch {
            mutableEffect.send(effect)
        }
    }
}
