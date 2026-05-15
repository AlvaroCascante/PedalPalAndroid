package com.quetoquenana.and.features.bikes.domain.model

data class BikeMediaUploadRequest(
    val name: String,
    val altText: String,
    val contentType: String,
    val isPrimary: Boolean,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BikeMediaUploadRequest

        if (isPrimary != other.isPrimary) return false
        if (name != other.name) return false
        if (altText != other.altText) return false
        if (contentType != other.contentType) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isPrimary.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + altText.hashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}
