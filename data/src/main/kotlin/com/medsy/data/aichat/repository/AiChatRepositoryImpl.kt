package com.medsy.data.aichat.repository

import com.medsy.data.aichat.local.AiChatImageStorage
import com.medsy.data.aichat.local.AiChatSessionDataSource
import com.medsy.data.aichat.mapper.toDomain
import com.medsy.data.aichat.remote.AiChatRemoteDataSource
import com.medsy.data.aichat.remote.ChatMessageRequestDto
import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatImage
import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.aichat.repository.AiChatRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.common.onError
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatRepositoryImpl @Inject constructor(
    private val remote: AiChatRemoteDataSource,
    private val session: AiChatSessionDataSource,
    private val images: AiChatImageStorage,
) : AiChatRepository {
    override fun observeSession(): Flow<com.medsy.domain.aichat.model.AiChatSession> = session.state

    override suspend fun loadHistory() = if (session.state.value.isHydrated) {
        MedsyResult.Success(Unit)
    } else {
        remote.getHistory()
            .map {
                session.hydrate(it.messages.orEmpty().mapNotNull { item -> item.toDomain() })
                Unit
            }
    }

    override suspend fun send(message: AiChatOutgoingMessage): MedsyResult<Unit, MedsyError.Remote> {
        if (session.state.value.isResponding) return MedsyResult.Success(Unit)
        val content = when (message) {
            is AiChatOutgoingMessage.Text -> AiChatContent.UserText(message.message)
            is AiChatOutgoingMessage.Image -> AiChatContent.UserText(
                value = message.caption,
                imageUri = message.image.uri,
            )
        }
        val generation = session.beginQuestion(content)
        val result = when (message) {
            is AiChatOutgoingMessage.Text -> remote.sendMessage(ChatMessageRequestDto(message.message))
            is AiChatOutgoingMessage.Image -> {
                val file = images.getFile(message.image)
                if (!file.exists()) {
                    session.failQuestion(generation)
                    return MedsyResult.Error(MedsyError.Remote.Unknown)
                }
                val mimeType = if (file.extension.equals("png", true)) "image/png" else "image/jpeg"
                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    file.asRequestBody(mimeType.toMediaType()),
                )
                val caption = message.caption.takeIf(String::isNotBlank)
                    ?.toRequestBody("text/plain".toMediaType())
                remote.sendImage(imagePart, caption)
            }
        }
        return result.map { response ->
            session.completeAnswer(generation, response.messageId, response.toDomain())
            Unit
        }.onError { session.failQuestion(generation) }
    }

    override suspend fun startNewChat() = remote.deleteHistory().map {
        session.clear()
        images.clear()
        Unit
    }

    override suspend fun prepareCameraImage(): MedsyResult<AiChatImage, MedsyError.Local> =
        localResult { images.prepareCameraImage() }

    override suspend fun importGalleryImage(uri: String): MedsyResult<AiChatImage, MedsyError.Local> =
        localResult { images.importGalleryImage(uri) }

    override suspend fun deleteImage(image: AiChatImage): MedsyResult<Unit, MedsyError.Local> =
        localResult { images.delete(image) }

    private inline fun <T> localResult(block: () -> T): MedsyResult<T, MedsyError.Local> = try {
        MedsyResult.Success(block())
    } catch (_: Exception) {
        MedsyResult.Error(MedsyError.Local.MEDIA)
    }
}
