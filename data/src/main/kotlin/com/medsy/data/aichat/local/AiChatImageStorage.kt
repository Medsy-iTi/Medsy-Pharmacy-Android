package com.medsy.data.aichat.local

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.medsy.domain.aichat.model.AiChatImage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatImageStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val directory: File
        get() = File(context.filesDir, "ai_chat").apply { mkdirs() }

    fun prepareCameraImage(): AiChatImage = File.createTempFile("ai_chat_", ".jpg", directory)
        .toImage()

    fun importGalleryImage(sourceUri: String): AiChatImage {
        val source = Uri.parse(sourceUri)
        val mimeType = context.contentResolver.getType(source)
        val extension = when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            else -> throw IllegalArgumentException("Unsupported AI chat image type")
        }
        val file = File.createTempFile("ai_chat_", extension, directory)
        try {
            context.contentResolver.openInputStream(source).use { input ->
                requireNotNull(input)
                file.outputStream().use { output -> input.copyTo(output) }
            }
        } catch (error: Exception) {
            file.delete()
            throw error
        }
        return file.toImage()
    }

    fun getFile(image: AiChatImage): File {
        val file = File(directory, image.storageKey).canonicalFile
        require(file.parentFile == directory.canonicalFile)
        return file
    }

    fun delete(image: AiChatImage) {
        getFile(image).delete()
    }

    fun clear() {
        directory.listFiles().orEmpty().forEach { it.delete() }
    }

    private fun File.toImage(): AiChatImage = AiChatImage(
        uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.ai_chat_files",
            this,
        ).toString(),
        storageKey = name,
    )
}
