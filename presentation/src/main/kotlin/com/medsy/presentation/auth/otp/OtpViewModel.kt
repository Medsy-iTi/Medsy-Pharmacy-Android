package com.medsy.presentation.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.VerifyOtpUseCase
import com.medsy.domain.common.MedsyResult
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(OtpUIState())
    val state = _state.asStateFlow()

    private val mutableEffect = Channel<OtpUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    init {
        startTimer()
    }

    fun setEmail(email: String) {
        if (_state.value.email.isEmpty() && email.isNotEmpty()) {
            _state.update { it.copy(email = email) }
        }
    }

    fun onIntent(intent: OtpUIIntent) {
        when (intent) {
            is OtpUIIntent.CodeChanged -> _state.update {
                it.copy(code = intent.value, hasError = false)
            }

            OtpUIIntent.Submit -> submit()
            OtpUIIntent.Resend -> startTimer()
            OtpUIIntent.Tick -> Unit
        }
    }

    private fun submit() {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, hasError = false) }

            when (val result = verifyOtpUseCase(_state.value.email, _state.value.code)) {
                is MedsyResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    mutableEffect.send(OtpUIEffect.NavigateNoPharmacy)
                }

                is MedsyResult.Error -> {
                    _state.update { it.copy(isLoading = false, hasError = true) }
                    mutableEffect.send(OtpUIEffect.ShowError(result.error.toMessageRes()))
                }
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            _state.update { it.copy(countdown = 300) }
            while (_state.value.countdown > 0) {
                delay(1000.milliseconds)
                _state.update { it.copy(countdown = it.countdown - 1) }
            }
        }
    }
}
