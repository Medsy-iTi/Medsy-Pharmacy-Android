package com.medsy.domain.aichat.usecase

import com.medsy.domain.aichat.repository.AiChatRepository
import javax.inject.Inject

class ObserveAiChatSessionUseCase @Inject constructor(private val repository: AiChatRepository) {
    operator fun invoke() = repository.observeSession()
}
