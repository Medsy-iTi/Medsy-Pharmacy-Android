package com.medsy.domain.aichat.usecase

import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.aichat.repository.AiChatRepository
import javax.inject.Inject

class SendAiChatMessageUseCase @Inject constructor(private val repository: AiChatRepository) {
    suspend operator fun invoke(message: AiChatOutgoingMessage) = repository.send(message)
}
