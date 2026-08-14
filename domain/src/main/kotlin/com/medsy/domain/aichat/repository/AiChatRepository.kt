package com.medsy.domain.aichat.repository

import com.medsy.domain.aichat.model.AiChatImage
import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.aichat.model.AiChatSession
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import kotlinx.coroutines.flow.Flow

interface AiChatRepository {
    fun observeSession(): Flow<AiChatSession>
    suspend fun loadHistory(): EmptyMedsyResult<MedsyError.Remote>
    suspend fun send(message: AiChatOutgoingMessage): EmptyMedsyResult<MedsyError.Remote>
    suspend fun startNewChat(): EmptyMedsyResult<MedsyError.Remote>
    suspend fun prepareCameraImage(): MedsyResult<AiChatImage, MedsyError.Local>
    suspend fun importGalleryImage(uri: String): MedsyResult<AiChatImage, MedsyError.Local>
    suspend fun deleteImage(image: AiChatImage): EmptyMedsyResult<MedsyError.Local>
}
