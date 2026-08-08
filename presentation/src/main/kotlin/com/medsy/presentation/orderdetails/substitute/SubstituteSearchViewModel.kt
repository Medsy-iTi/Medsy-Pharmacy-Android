package com.medsy.presentation.orderdetails.substitute

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.products.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubstituteSearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SubstituteSearchUIState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SubstituteSearchUIState(),
    )

    private val mutableEffect = Channel<SubstituteSearchUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    private var searchJob: Job? = null

    fun onIntent(intent: SubstituteSearchUIIntent) {
        when (intent) {
            is SubstituteSearchUIIntent.SearchQueryChanged -> {
                _state.update { it.copy(query = intent.query) }
                performSearch(intent.query)
            }

            is SubstituteSearchUIIntent.ProductSelected -> {
                viewModelScope.launch {
                    mutableEffect.send(
                        SubstituteSearchUIEffect.ReturnSubstitute(
                            intent.productId,
                            intent.productName,
                            intent.productPrice,
                            intent.productImage
                        )
                    )
                }
            }

            SubstituteSearchUIIntent.BackClicked -> {
                viewModelScope.launch {
                    mutableEffect.send(SubstituteSearchUIEffect.NavigateBack)
                }
            }
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(products = emptyList(), isLoading = false) }
            return
        }
        searchJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(500) // Debounce
            searchProductsUseCase(keyword = query, page = 0, size = 20)
                .onSuccess { page ->
                    _state.update { it.copy(isLoading = false, products = page.content) }
                }
                .onError {
                    _state.update { it.copy(isLoading = false, products = emptyList()) }
                }
        }
    }
}
