package com.medsy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.notifications.usecase.GetUnreadCountUseCase
import com.medsy.domain.orders.model.RequestStatusConstants
import com.medsy.domain.orders.usecase.GetPharmacyOrdersUseCase
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import com.medsy.presentation.common.util.toMessageRes
import com.medsy.presentation.requests.SubmittedOffersManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyPharmacy: GetMyPharmacyUseCase,
    private val getPharmacyOrdersUseCase: GetPharmacyOrdersUseCase,
    private val submittedOffersManager: SubmittedOffersManager,
    private val getUnreadCount: GetUnreadCountUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUIState())
    val state = _state.asStateFlow()

    private val mutableEffect = Channel<HomeUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    init {
        loadHomeData()
        observeSubmittedOffers()
    }

    private fun observeSubmittedOffers() {
        viewModelScope.launch {
            submittedOffersManager.submittedRequestIds.collect { submittedIds ->
                _state.update { currentState ->
                    val updatedOrders = currentState.latestOrders.map { order ->
                        val reqId = order.requestId.toLongOrNull() ?: -1L
                        if (submittedIds.contains(reqId) && order.status == HomeOrderStatus.NEW) {
                            order.copy(status = HomeOrderStatus.OFFER_SUBMITTED)
                        } else {
                            order
                        }
                    }
                    currentState.copy(latestOrders = updatedOrders)
                }
            }
        }
    }

    private fun loadHomeData() {
        _state.update { it.copy(isLoading = true) }
        loadPharmacyInfo()
        countNotifications()
    }

    private fun countNotifications() {
        viewModelScope.launch {
            getUnreadCount()
                .onSuccess { notificationCount ->
                    _state.update {
                        it.copy(notificationsCount = notificationCount)
                    }
                }
        }
    }

    private fun loadPharmacyInfo() {
        viewModelScope.launch {
            getMyPharmacy(forceRefresh = false)
                .onSuccess { pharmacy ->
                    loadOrders(pharmacy.id)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorRes = null,
                            pharmacyInfo = PharmacyUIInfo(
                                name = pharmacy.name,
                                address = pharmacy.address ?: "",
                                pharmacyId = pharmacy.id.toString(),
                                isOpen = true,
                            )
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorRes = error.toMessageRes()
                        )
                    }
                }
        }
    }

    private fun loadOrders(pharmacyId: Long) {
        viewModelScope.launch {
            getPharmacyOrdersUseCase(
                page = 0,
                size = 3,
                pharmacyId = pharmacyId,
                sort = listOf("id,desc")
            )
                .onSuccess { page ->
                    val newOrders = page.content.filter { req ->
                        req.status == RequestStatusConstants.PENDING ||
                                req.status == RequestStatusConstants.NEW ||
                                req.status == RequestStatusConstants.SEARCHING
                    }
                    val submitted = submittedOffersManager.submittedRequestIds.value
                    val latestThree = newOrders.map { req ->
                        var mappedStatus = when (req.status) {
                            RequestStatusConstants.PENDING, RequestStatusConstants.NEW, RequestStatusConstants.SEARCHING -> HomeOrderStatus.NEW
                            RequestStatusConstants.IN_PROGRESS -> HomeOrderStatus.PREPARING
                            RequestStatusConstants.DELIVERED -> HomeOrderStatus.DELIVERED
                            else -> HomeOrderStatus.NEW
                        }
                        if (submitted.contains(req.id) && mappedStatus == HomeOrderStatus.NEW) {
                            mappedStatus = HomeOrderStatus.OFFER_SUBMITTED
                        }
                        HomeOrderUI(
                            id = "#${req.id}",
                            requestId = req.id.toString(),
                            distanceKm = 0.0, // Distance not available on request
                            timeAgo = "",
                            status = mappedStatus
                        )
                    }

                    _state.update { currentState ->
                        currentState.copy(
                            latestOrders = latestThree,
                            stats = currentState.stats.copy(
                                newOrders = page.content.size,
                            )
                        )
                    }
                }
        }
    }

    fun onIntent(intent: HomeUIIntent) {
        when (intent) {
            HomeUIIntent.Refresh -> loadHomeData()
            is HomeUIIntent.OnOrderClicked -> {
                viewModelScope.launch {
                    mutableEffect.send(HomeUIEffect.NavigateToOrderDetails(intent.orderId))
                }
            }

            HomeUIIntent.OnViewAllOrdersClicked -> {
                viewModelScope.launch {
                    mutableEffect.send(HomeUIEffect.NavigateToViewAllOrders)
                }
            }

            HomeUIIntent.OnNotificationsClicked -> {
                viewModelScope.launch {
                    mutableEffect.send(HomeUIEffect.OpenNotifications)
                }
            }
        }
    }
}
