package com.medsy.domain.aichat.usecase

import com.medsy.domain.aichat.model.AiChatImage
import com.medsy.domain.aichat.repository.AiChatRepository
import javax.inject.Inject

class PrepareAiChatImageUseCase @Inject constructor(private val repository: AiChatRepository) {
    suspend operator fun invoke() = repository.prepareCameraImage()
}

class ImportAiChatImageUseCase @Inject constructor(private val repository: AiChatRepository) {
    suspend operator fun invoke(uri: String) = repository.importGalleryImage(uri)
}

class DeleteAiChatImageUseCase @Inject constructor(private val repository: AiChatRepository) {
    suspend operator fun invoke(image: AiChatImage) = repository.deleteImage(image)
}
