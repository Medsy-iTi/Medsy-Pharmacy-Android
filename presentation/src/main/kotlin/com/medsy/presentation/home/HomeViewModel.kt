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
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentPharmacist: GetCurrentPharmacistUseCase,
    private val getMyPharmacy: GetMyPharmacyUseCase,
    private val getCurrentPharmacyRequests: GetCurrentPharmacyRequestsUseCase,
    private val submittedOffersManager: com.medsy.presentation.orders.SubmittedOffersManager
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUIState())
    val state = _state.asStateFlow()

    private val mutableEffect = Channel<HomeUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    private var pollingJob: kotlinx.coroutines.Job? = null

    init {
        loadHomeData()
        prefetchProfileData()
        startPolling()
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

    private fun prefetchProfileData() {
        viewModelScope.launch {
            getCurrentPharmacist(forceRefresh = false)
            getMyPharmacy(forceRefresh = false)
        }
    }

    private fun startPolling() {
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(15000.milliseconds)
                pollOffersSilently()
            }
        }
    }

    private suspend fun pollOffersSilently() {
        val pharmacyIdStr = _state.value.pharmacyInfo.pharmacyId
        if (pharmacyIdStr.isBlank()) return
        
        val currentPharmacyId = pharmacyIdStr.removePrefix("PH").toLongOrNull() ?: return
        
        if (currentPharmacyId > 0) {
            val requestsResult = getCurrentPharmacyRequests(page = 0, size = 10, sort = listOf("id,desc"))
            requestsResult.fold(
                onSuccess = { page ->
                    val newOrders = page.content.filter { req ->
                        req.status == RequestStatusConstants.PENDING || 
                        req.status == RequestStatusConstants.NEW || 
                        req.status == RequestStatusConstants.SEARCHING 
                    }
                    val submitted = submittedOffersManager.submittedRequestIds.value
                    val latestThree = newOrders.take(3).map { req ->
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
                },
                onError = { }
            )
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
                            pharmacyId = pharmacy.id.toString(),
                            isOpen = true,
                        )
                    )
                },
                onError = { }
            )

            if (currentPharmacyId > 0) {
                val requestsResult = getCurrentPharmacyRequests(page = 0, size = 10, sort = listOf("id,desc"))
                requestsResult.fold(
                    onSuccess = { page ->
                        val newOrders = page.content.filter { req ->
                            req.status == RequestStatusConstants.PENDING || 
                            req.status == RequestStatusConstants.NEW || 
                            req.status == RequestStatusConstants.SEARCHING 
                        }
                        val submitted = submittedOffersManager.submittedRequestIds.value
                        val latestThree = newOrders.take(3).map { req ->
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
                                distanceKm = 0.0,
                                timeAgo = req.createdAt,
                                status = mappedStatus
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
            }

            _state.value = newState
        }
    }
}
