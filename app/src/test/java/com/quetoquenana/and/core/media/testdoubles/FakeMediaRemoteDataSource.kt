package com.quetoquenana.and.core.media.testdoubles

import com.quetoquenana.and.core.media.data.remote.dataSource.MediaRemoteDataSource
import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.util.UUID

/**
 * Fake implementation of MediaRemoteDataSource for unit tests.
 * - In-memory storage per (referenceId, referenceType)
 * - Deterministic creation of MediaFileResponseDto for uploads (correlationId preserved)
 * - Configurable error mode for negative tests
 */
class FakeMediaRemoteDataSource : MediaRemoteDataSource {

    private val mutex = Mutex()
    private val storage = mutableMapOf<Pair<UUID, MediaReferenceType>, MutableList<MediaFileResponseDto>>()

    @Volatile
    private var errorMode: Boolean = false

    fun setErrorMode(enabled: Boolean) {
        errorMode = enabled
    }

    suspend fun seedMedia(referenceId: UUID, referenceType: MediaReferenceType, media: List<MediaFileResponseDto>) {
        val key = referenceId to referenceType
        mutex.withLock {
            storage[key] = media.toMutableList()
        }
    }

    override suspend fun getMedia(referenceId: UUID, referenceType: MediaReferenceType): List<MediaFileResponseDto> {
        if (errorMode) throw RuntimeException("FakeMediaRemoteDataSource error")
        val key = referenceId to referenceType
        return mutex.withLock { storage[key]?.toList() ?: emptyList() }
    }

    override suspend fun createMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>
    ): List<MediaFileResponseDto> {
        if (errorMode) throw RuntimeException("FakeMediaRemoteDataSource error")

        return mutex.withLock {
            val created = uploads.map { upload ->
                val id = UUID.randomUUID()
                MediaFileResponseDto(
                    id = id,
                    correlationId = upload.correlationId,
                    contentType = upload.contentType,
                    provider = "fake",
                    status = "created",
                    name = upload.name,
                    altText = upload.altText,
                    url = "https://fake.media/$id",
                    expiresAt = Instant.now().plusSeconds(3600),
                    isPublic = upload.isPublic
                )
            }

            val key = referenceId to referenceType
            val list = storage.getOrPut(key) { mutableListOf() }
            list.addAll(created)
            created
        }
    }

    override suspend fun confirmMedia(mediaId: UUID): MediaFileResponseDto {
        if (errorMode) throw RuntimeException("FakeMediaRemoteDataSource error")

        return mutex.withLock {
            val entry = storage.values.flatten().indexOfFirst { it.id == mediaId }
            if (entry < 0) throw NoSuchElementException("Media not found: $mediaId")

            // find and update the DTO
            val list = storage.values.find { list -> list.any { it.id == mediaId } } ?: throw NoSuchElementException("Media not found: $mediaId")
            val index = list.indexOfFirst { it.id == mediaId }
            val existing = list[index]
            val confirmed = existing.copy(status = "confirmed")
            list[index] = confirmed
            confirmed
        }
    }
}
