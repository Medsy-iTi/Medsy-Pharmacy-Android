package com.medsy.presentation.auth.nopharmacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.auth.usecase.ClearSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoPharmacyViewModel @Inject constructor(
    private val clearSessionUseCase: ClearSessionUseCase,
) : ViewModel() {

    private val _effect = Channel<NoPharmacyEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: NoPharmacyIntent) {
        when (intent) {
            NoPharmacyIntent.RegisterPharmacy -> viewModelScope.launch {
                _effect.send(NoPharmacyEffect.NavigatePharmacyRegistration)
            }

            NoPharmacyIntent.SignOut -> viewModelScope.launch {
                clearSessionUseCase()
                _effect.send(NoPharmacyEffect.NavigateLogin)
            }
        }
    }
}
