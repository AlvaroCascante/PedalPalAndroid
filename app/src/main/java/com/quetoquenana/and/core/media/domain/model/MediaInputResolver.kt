package com.quetoquenana.and.core.media.domain.model

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.util.UUID

fun Context.toImageMediaUploadRequest(
    referenceId: UUID,
    uri: Uri,
    mediaType: MediaReferenceType,
): MediaUploadRequest? {
    return toImageMediaUploadRequests(
        referenceId = referenceId,
        uris = listOf(uri),
        mediaType = mediaType,
    ).firstOrNull()
}

fun Context.toImageMediaUploadRequests(
    referenceId: UUID,
    uris: List<Uri>,
    mediaType: MediaReferenceType,
    isPublic: Boolean = false,
): List<MediaUploadRequest> {
    return uris.mapIndexedNotNull { _, uri ->
        val contentType = resolveContentType(uri)
            ?.takeIf { it.startsWith(prefix = "image/") }
            ?: return@mapIndexedNotNull null
        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return@mapIndexedNotNull null
        val displayName = resolveDisplayName(uri)

        MediaUploadRequest(
            correlationId = UUID.randomUUID(),
            referenceId = referenceId,
            contentType = contentType,
            name = mediaType.mediaName,
            altText = displayName ?: mediaType.mediaName,
            bytes = bytes,
            isPublic = isPublic
        )
    }
}

private fun Context.resolveDisplayName(uri: Uri): String? {
    val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
    return contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        if (!cursor.moveToFirst()) return@use null
        val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (columnIndex < 0) return@use null
        cursor.getString(columnIndex)
    } ?: uri.lastPathSegment?.substringAfterLast('/')
}

private fun Context.resolveContentType(uri: Uri): String? {
    contentResolver.getType(uri)?.let { return it }
    val extension = resolveDisplayName(uri)
        ?.substringAfterLast('.', missingDelimiterValue = "")
        ?.lowercase()
        ?.takeIf { it.isNotBlank() }
        ?: return null
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

