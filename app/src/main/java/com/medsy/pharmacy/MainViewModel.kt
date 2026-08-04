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
import com.medsy.domain.auth.usecase.ObserveSessionUseCase
import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.notifications.usecase.MarkNotificationAsReadUseCase
import com.medsy.pharmacy.fcm.FcmTokenManager
import com.medsy.pharmacy.firebase.FCMTokenService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val observeSession: ObserveSessionUseCase,
    private val fcmTokenManager: FcmTokenManager,
    private val markNotificationAsRead: MarkNotificationAsReadUseCase,
) : ViewModel() {
    private var heartbeatJob: Job? = null

    private val _pendingRequestId = MutableStateFlow<Long?>(null)
    val pendingRequestId = _pendingRequestId.asStateFlow()

    fun consumePendingRequestId() {
        _pendingRequestId.value = null
    }

    fun handleNotificationIntent(intent: android.content.Intent) {
        var requestId = intent.getLongExtra(FCMTokenService.EXTRA_REQUEST_ID, -1L)
        var recipientId = intent.getLongExtra(FCMTokenService.EXTRA_RECIPIENT_ID, -1L)

        if (requestId == -1L) {
            val requestIdStr = intent.getStringExtra(FCMTokenService.KEY_REQUEST_ID)
                ?: intent.getStringExtra(FCMTokenService.KEY_ID)
            requestId = requestIdStr?.toLongOrNull() ?: -1L
        }

        if (recipientId == -1L) {
            val recipientIdStr = intent.getStringExtra(FCMTokenService.KEY_RECIPIENT_ID)
            recipientId = recipientIdStr?.toLongOrNull() ?: -1L
        }

        if (requestId != -1L) {
            _pendingRequestId.value = requestId
            Log.d("MainViewModel", "Parsed pendingRequestId from notification: $requestId")
        }

        if (recipientId != -1L) {
            viewModelScope.launch {
                markNotificationAsRead(recipientId)
                Log.d("MainViewModel", "Marked notification as read: $recipientId")
            }
        }
    }

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
        observeSessionAndRegisterToken()
    }

    private fun observeSessionAndRegisterToken() {
        viewModelScope.launch {
            observeSession().collect { session ->
                if (session != null && session.account.approvalStatus == PharmacyApprovalStatus.Approved) {
                    fcmTokenManager.registerDeviceToken()
                }
            }
        }
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
