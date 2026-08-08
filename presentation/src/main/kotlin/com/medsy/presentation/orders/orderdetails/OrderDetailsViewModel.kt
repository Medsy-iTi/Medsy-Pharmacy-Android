package com.medsy.presentation.orders.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.model.OrderItemDomain
import com.medsy.domain.orders.model.PharmacyOrderDomain
import com.medsy.domain.orders.usecase.GetPharmacyOrderDetailsUseCase
import com.medsy.presentation.orderdetails.model.PaymentMethod
import com.medsy.presentation.orderdetails.model.Request
import com.medsy.presentation.orderdetails.model.RequestMedicineItem
import com.medsy.presentation.requests.calculateMinutesAgoOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val getOrderDetails: GetPharmacyOrderDetailsUseCase,
) : ViewModel() {
    val state = MutableStateFlow(OrderDetailsUIState())
    private val mutableEffect = Channel<OrderDetailsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()
    private var orderId: Long? = null

    fun onIntent(intent: OrderDetailsUIIntent) {
        when (intent) {
            is OrderDetailsUIIntent.Load -> if (orderId != intent.orderId) {
                orderId = intent.orderId
                load(intent.orderId)
            }

            OrderDetailsUIIntent.Retry -> orderId?.let(::load)
            OrderDetailsUIIntent.BackClicked -> sendEffect(OrderDetailsUIEffect.NavigateBack)
            OrderDetailsUIIntent.CallCustomerClicked -> state.value.order?.customerPhone
                ?.takeIf(String::isNotBlank)
                ?.let { sendEffect(OrderDetailsUIEffect.DialPhoneNumber(it)) }

            OrderDetailsUIIntent.OpenLocationClicked -> state.value.order?.let { order ->
                val latitude = order.deliveryLatitude ?: return@let
                val longitude = order.deliveryLongitude ?: return@let
                sendEffect(OrderDetailsUIEffect.OpenLocation(latitude, longitude))
            }
        }
    }

    private fun load(id: Long) {
        viewModelScope.launch {
            state.update { it.copy(isLoading = true, hasError = false) }
            getOrderDetails(id)
                .onSuccess { order ->
                    state.value = OrderDetailsUIState(
                        isLoading = false,
                        order = order.toPresentation(),
                        status = order.status,
                        minutesAgo = calculateMinutesAgoOrNull(order.createdAt),
                    )
                }
                .onError { state.update { it.copy(isLoading = false, hasError = true) } }
        }
    }

    private fun sendEffect(effect: OrderDetailsUIEffect) {
        viewModelScope.launch { mutableEffect.send(effect) }
    }
}

private fun PharmacyOrderDomain.toPresentation() = Request(
    id = id.toString(),
    isNew = false,
    minutesAgo = calculateMinutesAgoOrNull(createdAt) ?: 0,
    customerName = customerName,
    customerId = customerId,
    customerPhone = customerPhone.orEmpty(),
    customerAddress = deliveryAddress.orEmpty(),
    deliveryLatitude = deliveryLatitude,
    deliveryLongitude = deliveryLongitude,
    items = items.map(OrderItemDomain::toPresentation),
    customerNotes = customerNotes,
    total = total,
    prescriptionUrl = prescriptionUrl,
    paymentMethod = PaymentMethod.fromApiValue(paymentMethod),
)

private fun OrderItemDomain.toPresentation(): RequestMedicineItem {
    val packInfo =
        listOfNotNull(strength, packSize, form).filter(String::isNotBlank).joinToString(" • ")
    return RequestMedicineItem(
        id.toString(),
        productName,
        packInfo,
        quantity,
        unitPrice,
        imageUrl,
        productId
    )
}
