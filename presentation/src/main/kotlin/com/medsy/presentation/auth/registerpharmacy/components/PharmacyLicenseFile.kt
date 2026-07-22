package com.medsy.presentation.auth.registerpharmacy.components

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream

internal const val PDF_MEDIA_TYPE = "application/pdf"
internal const val DEFAULT_LICENSE_DISPLAY_NAME = "license.pdf"
internal const val MAX_LICENSE_PDF_BYTES = 1_048_576

internal fun ContentResolver.readSelectedLicense(uri: Uri): LicenseReadResult {
    val displayName = queryOpenableString(uri, OpenableColumns.DISPLAY_NAME)
        ?: DEFAULT_LICENSE_DISPLAY_NAME
    val knownSize = queryOpenableLong(uri, OpenableColumns.SIZE)
    val mimeType = getType(uri)

    if (knownSize != null && knownSize > MAX_LICENSE_PDF_BYTES) {
        return LicenseReadResult.TooLarge
    }

    return when (val bytesResult = readBytesWithLimit(uri, MAX_LICENSE_PDF_BYTES)) {
        LicenseBytesResult.ReadError -> LicenseReadResult.ReadError
        LicenseBytesResult.TooLarge -> LicenseReadResult.TooLarge
        is LicenseBytesResult.Success -> LicenseReadResult.Success(
            displayName = displayName,
            mimeType = mimeType,
            bytes = bytesResult.bytes,
        )
    }
}

private fun ContentResolver.queryOpenableString(uri: Uri, columnName: String): String? =
    query(uri, arrayOf(columnName), null, null, null)?.use { cursor ->
        if (!cursor.moveToFirst()) return@use null
        val index = cursor.getColumnIndex(columnName)
        if (index >= 0) cursor.getString(index) else null
    }

private fun ContentResolver.queryOpenableLong(uri: Uri, columnName: String): Long? =
    query(uri, arrayOf(columnName), null, null, null)?.use { cursor ->
        if (!cursor.moveToFirst()) return@use null
        val index = cursor.getColumnIndex(columnName)
        if (index >= 0 && !cursor.isNull(index)) cursor.getLong(index) else null
    }

private fun ContentResolver.readBytesWithLimit(
    uri: Uri,
    maxBytes: Int,
): LicenseBytesResult = try {
    openInputStream(uri)?.use { input ->
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(8 * 1024)
        var total = 0

        while (true) {
            val read = input.read(buffer)
            if (read == -1) break
            total += read
            if (total > maxBytes) return LicenseBytesResult.TooLarge
            output.write(buffer, 0, read)
        }

        LicenseBytesResult.Success(output.toByteArray())
    } ?: LicenseBytesResult.ReadError
} catch (_: Exception) {
    LicenseBytesResult.ReadError
}

internal sealed interface LicenseReadResult {
    data class Success(
        val displayName: String,
        val mimeType: String?,
        val bytes: ByteArray,
    ) : LicenseReadResult

    data object TooLarge : LicenseReadResult
    data object ReadError : LicenseReadResult
}

private sealed interface LicenseBytesResult {
    data class Success(val bytes: ByteArray) : LicenseBytesResult
    data object TooLarge : LicenseBytesResult
    data object ReadError : LicenseBytesResult
}
