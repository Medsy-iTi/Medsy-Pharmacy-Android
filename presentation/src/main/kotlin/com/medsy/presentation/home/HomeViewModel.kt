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
import com.medsy.domain.offer.usecase.GetPharmacyOffersUseCase
import com.medsy.domain.pharmacist.usecase.GetCurrentPharmacistUseCase
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentPharmacist: GetCurrentPharmacistUseCase,
    private val getMyPharmacy: GetMyPharmacyUseCase,
    private val getPharmacyOffers: GetPharmacyOffersUseCase
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

            val pharmacyResult = getMyPharmacy()
            
            var newState = _state.value.copy(isLoading = false)
            var currentPharmacyId: Long = 0L

            pharmacyResult.fold(
                onSuccess = { pharmacy ->
                    currentPharmacyId = pharmacy.id
                    newState = newState.copy(
                        pharmacyInfo = newState.pharmacyInfo.copy(
                            name = pharmacy.name,
                            address = pharmacy.address ?: "",
                            pharmacyId = "PH${pharmacy.id}",
                            isOpen = true, // Should ideally come from presence
                            closingTime = "11:00 مساءً",
                            rating = 4.8,
                            reviewsCount = 256
                        )
                    )
                },
                onError = { }
            )

            if (currentPharmacyId > 0) {
                val offersResult = getPharmacyOffers(pharmacyId = currentPharmacyId, page = 0, size = 10)
                offersResult.fold(
                    onSuccess = { page ->
                        val latestThree = page.content.take(3).map { offer ->
                            HomeOrderUI(
                                id = "#${offer.id}",
                                customerName = "عرض لطلب #${offer.requestId}",
                                location = "${offer.distanceKm} كم",
                                timeAgo = "", // We can add createdAt to domain model later
                                status = when (offer.status) {
                                    "PENDING" -> HomeOrderStatus.NEW
                                    "ACCEPTED" -> HomeOrderStatus.PREPARING
                                    "COMPLETED" -> HomeOrderStatus.DELIVERED
                                    else -> HomeOrderStatus.NEW
                                }
                            )
                        }

                        newState = newState.copy(
                            latestOrders = latestThree,
                            stats = newState.stats.copy(
                                newOrders = page.content.count { it.status == "PENDING" },
                                inProgress = page.content.count { it.status == "ACCEPTED" }
                            )
                        )
                    },
                    onError = { }
                )
            }

            _state.value = newState
        }
    }
}
