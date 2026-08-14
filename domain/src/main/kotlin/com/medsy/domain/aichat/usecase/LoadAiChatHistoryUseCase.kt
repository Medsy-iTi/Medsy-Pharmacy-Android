package com.medsy.domain.aichat.usecase

import com.medsy.domain.aichat.repository.AiChatRepository
import javax.inject.Inject

class LoadAiChatHistoryUseCase @Inject constructor(private val repository: AiChatRepository) {
    suspend operator fun invoke() = repository.loadHistory()
}
