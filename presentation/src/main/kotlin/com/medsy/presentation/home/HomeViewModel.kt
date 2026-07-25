package com.medsy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.medsy.domain.common.fold
import com.medsy.domain.orders.model.RequestStatusConstants
import com.medsy.domain.orders.usecase.GetCurrentPharmacyRequestsUseCase
import com.medsy.domain.pharmacist.usecase.GetCurrentPharmacistUseCase
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentPharmacist: GetCurrentPharmacistUseCase,
    private val getMyPharmacy: GetMyPharmacyUseCase,
    private val getCurrentPharmacyRequests: GetCurrentPharmacyRequestsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUIState())
    val state = _state.asStateFlow()

    private val mutableEffect = Channel<HomeUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    init {
        loadHomeData()
        prefetchProfileData()
    }

    private fun prefetchProfileData() {
        viewModelScope.launch {
            getCurrentPharmacist(forceRefresh = false)
            getMyPharmacy(forceRefresh = false)
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

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val pharmacyDeferred = async { getMyPharmacy() }
            val ordersDeferred = async { getCurrentPharmacyRequests(page = 0, size = 10) }

            val pharmacyResult = pharmacyDeferred.await()
            val ordersResult = ordersDeferred.await()

            var newState = _state.value.copy(isLoading = false)

            pharmacyResult.fold(
                onSuccess = { pharmacy ->
                    newState = newState.copy(
                        pharmacyInfo = newState.pharmacyInfo.copy(
                            name = pharmacy.name,
                            address = pharmacy.address ?: "",
                            pharmacyId = "PH${pharmacy.id}",
                            isOpen = true,
                            closingTime = "11:00 مساءً",
                            rating = 4.8,
                            reviewsCount = 256
                        )
                    )
                },
                onError = { }
            )

            ordersResult.fold(
                onSuccess = { page ->
                    val latestThree = page.content.take(3).map { order ->
                        HomeOrderUI(
                            id = "#${order.id}",
                            customerName = "Customer #${order.customerId}",
                            location = order.deliveryAddress ?: "No address",
                            timeAgo = order.createdAt,
                            status = when (order.status) {
                                RequestStatusConstants.PENDING, RequestStatusConstants.NEW -> HomeOrderStatus.NEW
                                RequestStatusConstants.IN_PROGRESS -> HomeOrderStatus.PREPARING
                                RequestStatusConstants.DELIVERED -> HomeOrderStatus.DELIVERED
                                else -> HomeOrderStatus.NEW
                            }
                        )
                    }

                    newState = newState.copy(
                        latestOrders = latestThree,
                        stats = newState.stats.copy(
                            newOrders = page.content.count {
                                it.status == RequestStatusConstants.PENDING || it.status == RequestStatusConstants.NEW
                            },
                            inProgress = page.content.count { it.status == RequestStatusConstants.IN_PROGRESS }
                        )
                    )
                },
                onError = { }
            )

            _state.value = newState
        }
    }
}
