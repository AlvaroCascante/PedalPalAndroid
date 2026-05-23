package com.quetoquenana.and.core.media.domain.model

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap

fun Context.toImageMediaUploadRequest(
    uri: Uri,
    purpose: MediaReferenceType,
): MediaUploadRequest? {
    return toImageMediaUploadRequests(
        uris = listOf(uri),
        purpose = purpose,
    ).firstOrNull()
}

fun Context.toImageMediaUploadRequests(
    uris: List<Uri>,
    purpose: MediaReferenceType,
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
            name = purpose.mediaName,
            altText = displayName ?: purpose.mediaName,
            contentType = contentType,
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

