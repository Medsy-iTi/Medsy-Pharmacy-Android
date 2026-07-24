package com.medsy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.medsy.domain.common.fold
import com.medsy.domain.pharmacist.usecase.GetCurrentPharmacistUseCase
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentPharmacist: GetCurrentPharmacistUseCase,
    private val getMyPharmacy: GetMyPharmacyUseCase
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

            val result = getMyPharmacy()

            result.fold(
                onSuccess = { pharmacy ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            pharmacyInfo = it.pharmacyInfo.copy(
                                name = pharmacy.name,
                                address = pharmacy.address ?: "",
                                pharmacyId = "PH${pharmacy.id}",
                                isOpen = true,
                                closingTime = "11:00 مساءً",
                                rating = 4.8,
                                reviewsCount = 256
                            ),
                            notificationsCount = 3,
                            stats = HomeStatsUI(
                                newOrders = 23,
                                inProgress = 18,
                                deliveredToday = 45,
                                totalSales = "3,240"
                            ),
                            latestOrders = listOf(
                                HomeOrderUI(
                                    "#1258",
                                    "Ahlam Gomaa",
                                    "المعادي، القاهرة",
                                    " 10 Seconds",
                                    HomeOrderStatus.NEW
                                ),
                                HomeOrderUI(
                                    "#1257",
                                    "Eman Gomaa",
                                    "شارع النيل، المعادي",
                                    " 15 Minutes",
                                    HomeOrderStatus.PREPARING
                                ),
                                HomeOrderUI(
                                    "#1256",
                                    "Menna Mohamed",
                                    "دار السلام، القاهرة",
                                    "35 Minutes",
                                    HomeOrderStatus.DELIVERED
                                )
                            )
                        )
                    }
                },
                onError = {
                    _state.update { it.copy(isLoading = false) }
                }
            )
        }
    }
}
