package com.medsy.pharmacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import com.medsy.domain.common.preferences.usecase.SetReceivingOrdersPreferenceUseCase
import com.medsy.domain.pharmacist.usecase.SendHeartbeatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Log
import com.medsy.domain.common.fold
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

data class MainState(
    val themeMode: ThemeMode? = null,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observePreferences: ObserveUserPreferencesUseCase,
    private val sendHeartbeat: SendHeartbeatUseCase,
    private val setReceivingOrdersPreference: SetReceivingOrdersPreferenceUseCase,
) : ViewModel() {
    private var heartbeatJob: Job? = null

    val state = observePreferences()
        .map { MainState(themeMode = it.themeMode) }
        .catch { emit(MainState(themeMode = ThemeMode.System)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MainState(),
        )

    init {
        observePresence()
    }

    private fun observePresence() {
        viewModelScope.launch {
            observePreferences().collect { prefs ->
                if (prefs.isReceivingOrders) {
                    startHeartbeat()
                } else {
                    stopHeartbeat()
                }
            }
        }
    }

    private fun startHeartbeat() {
        if (heartbeatJob?.isActive == true) return
        heartbeatJob = viewModelScope.launch {
            while (true) {
                val result = sendHeartbeat()
                result.fold(
                    onSuccess = { status ->
                        Log.d("MainViewModel", "Heartbeat sent: onDuty=${status.onDuty}")
                        if (!status.onDuty) {
                            setReceivingOrdersPreference(false)
                        }
                    },
                    onError = { error ->
                        Log.e("MainViewModel", "Heartbeat failed: $error")
                    }
                )
                delay(60_000.milliseconds)
            }
        }
    }

    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }
}
