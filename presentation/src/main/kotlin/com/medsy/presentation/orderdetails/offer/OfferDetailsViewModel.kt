package com.medsy.presentation.orderdetails.offer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.offer.usecase.GetOfferByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfferDetailsViewModel @Inject constructor(
    private val getOfferById: GetOfferByIdUseCase,
) : ViewModel() {
    val state = MutableStateFlow(OfferDetailsUIState())
    private val mutableEffect = Channel<OfferDetailsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()
    private var offerId: Long? = null

    fun onIntent(intent: OfferDetailsUIIntent) {
        when (intent) {
            is OfferDetailsUIIntent.Load -> if (offerId != intent.offerId) {
                offerId = intent.offerId
                load(intent.offerId)
            }

            OfferDetailsUIIntent.Retry -> offerId?.let(::load)
            OfferDetailsUIIntent.BackClicked -> viewModelScope.launch {
                mutableEffect.send(
                    OfferDetailsUIEffect.NavigateBack
                )
            }
        }
    }

    private fun load(id: Long) {
        viewModelScope.launch {
            state.update { it.copy(isLoading = true, hasError = false) }
            getOfferById(id)
                .onSuccess { offer ->
                    state.value = OfferDetailsUIState(isLoading = false, offer = offer)
                }
                .onError { state.update { it.copy(isLoading = false, hasError = true) } }
        }
    }
}
