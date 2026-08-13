package com.medsy.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.fold
import com.medsy.domain.notifications.model.NotificationDomain
import com.medsy.domain.notifications.usecase.GetNotificationsUseCase
import com.medsy.domain.notifications.usecase.MarkAllNotificationsAsReadUseCase
import com.medsy.domain.notifications.usecase.MarkNotificationAsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markAsReadUseCase: MarkNotificationAsReadUseCase,
    private val markAllAsReadUseCase: MarkAllNotificationsAsReadUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state = _state.asStateFlow()

    private val mutableEffect = Channel<NotificationsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    init {
        loadNotifications()
    }
    companion object{
        const val KEY_REQUEST_ID = "requestId"
        const val KEY_ID = "id"
        const val KEY_ORDER_ID = "orderId"
        const val KEY_LEGACY_ORDER_ID = "orderID"
        const val CATEGORY_REQUEST_IN_AREA = "REQUEST_IN_AREA"
        const val CATEGORY_ORDER_CREATED = "ORDER_CREATED"
    }

    fun onIntent(intent: NotificationsUIIntent) {
        when (intent) {
            NotificationsUIIntent.LoadNotifications -> loadNotifications()
            NotificationsUIIntent.BackClicked -> {
                viewModelScope.launch {
                    mutableEffect.send(NotificationsUIEffect.NavigateBack)
                }
            }

            NotificationsUIIntent.MarkAllAsReadClicked -> markAllAsRead()
            is NotificationsUIIntent.NotificationClicked -> handleNotificationClicked(intent.recipientId)
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            val result = getNotificationsUseCase(page = 0, size = 50)
            result.fold(
                onSuccess = { page ->
                    _state.update { it.copy(isLoading = false, notifications = page.content) }
                },
                onError = { error ->
                    _state.update { it.copy(isLoading = false, errorMsg = error.toString()) }
                }
            )
        }
    }

    private fun markAllAsRead() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = markAllAsReadUseCase()
            result.fold(
                onSuccess = {
                    loadNotifications()
                },
                onError = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = error.toString()
                        )
                    }
                }
            )
        }
    }

    private fun handleNotificationClicked(recipientId: Long) {
        viewModelScope.launch {
            val notification = _state.value.notifications.find { it.recipientId == recipientId }
            if (notification != null) {
                if (!notification.isRead) {
                    markAsReadUseCase(recipientId)
                    _state.update { currentState ->
                        currentState.copy(
                            notifications = currentState.notifications.map { item ->
                                if (item.recipientId == recipientId) {
                                    item.copy(status = NotificationDomain.STATUS_READ)
                                } else {
                                    item
                                }
                            }
                        )
                    }
                }

                when (notification.category.uppercase()) {
                    CATEGORY_REQUEST_IN_AREA -> {
                        val requestId = (
                            notification.dataPayload[KEY_REQUEST_ID] ?: notification.dataPayload[KEY_ID]
                        )?.toLongOrNull()
                        if (requestId != null) {
                            mutableEffect.send(NotificationsUIEffect.NavigateToRequestDetails(requestId))
                        }
                    }
                    CATEGORY_ORDER_CREATED -> {
                        val orderId = (
                            notification.dataPayload[KEY_ORDER_ID] ?:
                                notification.dataPayload[KEY_LEGACY_ORDER_ID]
                        )?.toLongOrNull()
                        if (orderId != null) {
                            mutableEffect.send(NotificationsUIEffect.NavigateToOrderDetails(orderId))
                        }
                    }
                }
            }
        }
    }
}
