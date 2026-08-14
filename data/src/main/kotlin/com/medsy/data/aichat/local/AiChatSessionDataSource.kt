package com.medsy.data.aichat.local

import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatMessage
import com.medsy.domain.aichat.model.AiChatSender
import com.medsy.domain.aichat.model.AiChatSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatSessionDataSource @Inject constructor() {
    private val mutableState = MutableStateFlow(AiChatSession())
    val state = mutableState.asStateFlow()
    private var nextLocalId = -1L
    private var sessionGeneration = 0L

    @Synchronized
    fun hydrate(messages: List<AiChatMessage>) {
        mutableState.update { current ->
            if (current.isHydrated || current.messages.isNotEmpty()) {
                current.copy(isHydrated = true)
            } else {
                current.copy(messages = messages, isHydrated = true)
            }
        }
    }

    @Synchronized
    fun beginQuestion(content: AiChatContent.UserText): Long {
        val generation = sessionGeneration
        mutableState.update { current ->
            val trailingQuestion = current.messages.lastOrNull()?.content as? AiChatContent.UserText
            val messages = if (trailingQuestion == content) {
                current.messages
            } else {
                current.messages + AiChatMessage(
                    id = nextLocalId--,
                    sender = AiChatSender.USER,
                    content = content,
                )
            }
            current.copy(
                messages = messages,
                isResponding = true,
            )
        }
        return generation
    }

    @Synchronized
    fun completeAnswer(
        generation: Long,
        serverMessageId: Long?,
        response: AiChatContent.AssistantMessage,
    ) {
        if (generation != sessionGeneration) return
        mutableState.update { current ->
            current.copy(
                messages = current.messages + AiChatMessage(
                    serverMessageId ?: nextLocalId--,
                    AiChatSender.ASSISTANT,
                    response,
                ),
                isResponding = false,
            )
        }
    }

    @Synchronized
    fun failQuestion(generation: Long) {
        if (generation != sessionGeneration) return
        mutableState.update { it.copy(isResponding = false) }
    }

    @Synchronized
    fun clear() {
        sessionGeneration += 1L
        nextLocalId = -1L
        mutableState.value = AiChatSession(isHydrated = true)
    }
}
