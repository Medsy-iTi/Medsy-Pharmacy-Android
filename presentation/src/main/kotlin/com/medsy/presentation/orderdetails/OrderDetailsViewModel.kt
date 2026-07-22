package com.medsy.presentation.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.model.Order
import com.medsy.presentation.orderdetails.model.OrderMedicineItem
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
class OrderDetailsViewModel @Inject constructor() : ViewModel() {

    private var loadedOrderId: String? = null

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
                if (loadedOrderId != intent.orderId) {
                    loadedOrderId = intent.orderId
                    loadOrder(intent.orderId)
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

    private fun loadOrder(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(300)

            val order = Order(
                id = id,
                isNew = true,
                minutesAgo = 5,
                customerName = "Omar Ramadan",
                customerPhone = "01152267125",
                customerAddress = "Nile St, Maadi, Cairo",
                items = listOf(
                    OrderMedicineItem(
                        id = "panadol-extra",
                        name = "Panadol Extra",
                        packInfo = "500 mg · 24 Tablets",
                        quantity = 2,
                        price = 68,
                        imageUrl = "https://example.com/images/panadol_extra.png",
                    ),
                    OrderMedicineItem(
                        id = "augmentin-1g",
                        name = "Augmentin 1g",
                        packInfo = "14 Tablets",
                        quantity = 1,
                        price = 120,
                        imageUrl = "https://example.com/images/augmentin_1g.png",
                    ),
                    OrderMedicineItem(
                        id = "vitamin-c-1000",
                        name = "Vitamin C 1000mg",
                        packInfo = "20 Effervescent Tablets",
                        quantity = 1,
                        price = 75,
                        imageUrl = "https://example.com/images/vitamin_c_1000.png",
                    ),
                ),
                customerNotes = "Please deliver the order after 5 PM.",
                total = 263,
            )

            _state.update { it.copy(isLoading = false, order = order) }
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
