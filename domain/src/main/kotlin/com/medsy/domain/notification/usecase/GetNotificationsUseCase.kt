package com.medsy.domain.notification.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.notification.model.PaginatedNotifications
import com.medsy.domain.notification.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(
        status: String? = null,
        page: Int = 0,
        size: Int = 20,
        sort: List<String> = emptyList()
    ): MedsyResult<PaginatedNotifications, MedsyError> {
        return repository.getNotifications(status, page, size, sort)
    }
}
