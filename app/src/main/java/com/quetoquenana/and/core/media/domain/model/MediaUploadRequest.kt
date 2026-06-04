package com.quetoquenana.and.core.media.domain.model

import java.util.UUID

data class MediaUploadRequest(
    val correlationId: UUID,
    val referenceId: UUID,
    val contentType: String,
    val name: String,
    val altText: String,
    val bytes: ByteArray,
    val isPublic: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaUploadRequest

        if (referenceId != other.referenceId) return false
        if (name != other.name) return false
        if (altText != other.altText) return false
        if (contentType != other.contentType) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + referenceId.hashCode()
        result = 31 * result + altText.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}

