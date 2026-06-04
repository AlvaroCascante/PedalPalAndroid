package com.quetoquenana.and.core.media.data.repository

import com.quetoquenana.and.core.media.data.local.datasource.MediaLocalDataSource
import com.quetoquenana.and.core.media.data.local.entity.MediaEntity
import com.quetoquenana.and.core.media.data.local.entity.toEntity
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaRemoteDataSource
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaUploadRemoteDataSource
import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.util.UUID

class MediaRepositoryImplTest {

    @Test
    fun `observeMedia refreshes when cached media is expired`() = runTest {
        val expiredMedia = mediaEntity(
            url = "https://example.com/expired.png",
            urlExpireAt = Instant.parse("2020-01-01T00:00:00Z")
        )
        val refreshedResponse = listOf(
            mediaFileResponseDto(
                id = MEDIA_ID_A,
                correlationId = CORRELATION_ID_A,
                url = "https://example.com/refreshed.png",
            )
        )
        val local = FakeMediaLocalDataSource(initialMedia = listOf(expiredMedia))
        val remote = FakeMediaRemoteDataSource(
            createMediaResponse = emptyList(),
            getMediaResponse = refreshedResponse,
        )
        val repository = MediaRepositoryImpl(
            local = local,
            remote = remote,
            uploadRemote = FakeMediaUploadRemoteDataSource(),
        )

        val result = repository.observeMedia(
            referenceId = REFERENCE_ID,
            referenceType = MediaReferenceType.PROFILE,
        ).first()

        assertEquals(1, remote.getMediaCallCount)
        assertEquals(listOf("https://example.com/refreshed.png"), result.map { it.url })
        assertEquals(listOf("https://example.com/refreshed.png"), local.savedMedia.map { it.url })
    }

    @Test
    fun `observeMedia does not refresh when cached media is still valid`() = runTest {
        val validMedia = mediaEntity(
            url = "https://example.com/current.png",
            urlExpireAt = Instant.parse("9999-12-31T23:59:59Z"),
        )
        val local = FakeMediaLocalDataSource(initialMedia = listOf(validMedia))
        val remote = FakeMediaRemoteDataSource(createMediaResponse = emptyList())
        val repository = MediaRepositoryImpl(
            local = local,
            remote = remote,
            uploadRemote = FakeMediaUploadRemoteDataSource(),
        )

        val result = repository.observeMedia(
            referenceId = REFERENCE_ID,
            referenceType = MediaReferenceType.PROFILE,
        ).first()

        assertEquals(0, remote.getMediaCallCount)
        assertEquals(listOf("https://example.com/current.png"), result.map { it.url })
    }

    @Test
    fun `uploadMedia matches remote items using correlationId`() = runTest {
        val local = FakeMediaLocalDataSource()
        val uploadRemote = FakeMediaUploadRemoteDataSource()
        val remote = FakeMediaRemoteDataSource(
            createMediaResponse = listOf(
                mediaFileResponseDto(
                    id = MEDIA_ID_B,
                    correlationId = CORRELATION_ID_B,
                    name = "second-on-server.png",
                    url = "https://example.com/b.png",
                ),
                mediaFileResponseDto(
                    id = MEDIA_ID_A,
                    correlationId = CORRELATION_ID_A,
                    name = "first-on-server.png",
                    url = "https://example.com/a.png",
                ),
            ),
        )
        val repository = MediaRepositoryImpl(
            local = local,
            remote = remote,
            uploadRemote = uploadRemote,
        )
        val uploads = listOf(
            mediaUploadRequest(
                correlationId = CORRELATION_ID_A,
                name = "camera-a.png",
                bytes = byteArrayOf(1),
            ),
            mediaUploadRequest(
                correlationId = CORRELATION_ID_B,
                name = "camera-b.png",
                bytes = byteArrayOf(2),
            ),
        )

        repository.uploadMedia(
            referenceId = REFERENCE_ID,
            referenceType = MediaReferenceType.PROFILE,
            uploads = uploads,
        )

        assertEquals(listOf("https://example.com/a.png", "https://example.com/b.png"), uploadRemote.uploadedUrls)
        assertEquals(listOf(MEDIA_ID_A, MEDIA_ID_B), remote.confirmedMediaIds)
        assertEquals(listOf(MEDIA_ID_A, MEDIA_ID_B), local.savedMedia.map { it.mediaId })
        assertEquals(listOf("https://example.com/a-confirmed.png", "https://example.com/b-confirmed.png"), local.savedMedia.map { it.url })
    }

    @Test
    fun `uploadMedia skips upload when matched remote url is blank`() = runTest {
        val local = FakeMediaLocalDataSource()
        val uploadRemote = FakeMediaUploadRemoteDataSource()
        val remote = FakeMediaRemoteDataSource(
            createMediaResponse = listOf(
                mediaFileResponseDto(
                    id = MEDIA_ID_A,
                    correlationId = CORRELATION_ID_A,
                    url = "",
                ),
            ),
        )
        val repository = MediaRepositoryImpl(
            local = local,
            remote = remote,
            uploadRemote = uploadRemote,
        )

        repository.uploadMedia(
            referenceId = REFERENCE_ID,
            referenceType = MediaReferenceType.PROFILE,
            uploads = listOf(mediaUploadRequest(correlationId = CORRELATION_ID_A)),
        )

        assertEquals(emptyList<String>(), uploadRemote.uploadedUrls)
        assertEquals(emptyList<UUID>(), remote.confirmedMediaIds)
        assertEquals(emptyList<MediaEntity>(), local.savedMedia)
    }

    private class FakeMediaLocalDataSource(
        initialMedia: List<MediaEntity> = emptyList(),
    ) : MediaLocalDataSource {
        private val mediaState = MutableStateFlow(initialMedia)
        var savedMedia: List<MediaEntity> = initialMedia

        override fun observeMedia(referenceId: UUID, referenceType: String): Flow<List<MediaEntity>> = mediaState

        override suspend fun getMedia(referenceId: UUID, referenceType: String): List<MediaEntity> = mediaState.value

        override suspend fun saveAllMedia(media: List<MediaEntity>) {
            savedMedia = media
            mediaState.value = media
        }

        override suspend fun updateMedia(media: MediaEntity) {
            savedMedia = savedMedia
                .filterNot { existing ->
                    existing.referenceId == media.referenceId &&
                        existing.referenceType == media.referenceType &&
                        existing.mediaId == media.mediaId
                } + media
            mediaState.value = savedMedia
        }

        override suspend fun clearMedia() = Unit
    }

    private class FakeMediaRemoteDataSource(
        private val createMediaResponse: List<MediaFileResponseDto>,
        private val getMediaResponse: List<MediaFileResponseDto> = emptyList(),
    ) : MediaRemoteDataSource {
        val confirmedMediaIds = mutableListOf<UUID>()
        var getMediaCallCount = 0

        override suspend fun getMedia(
            referenceId: UUID,
            referenceType: MediaReferenceType,
        ): List<MediaFileResponseDto> {
            getMediaCallCount += 1
            return getMediaResponse
        }

        override suspend fun createMedia(
            referenceId: UUID,
            referenceType: MediaReferenceType,
            uploads: List<MediaUploadRequest>,
        ): List<MediaFileResponseDto> = createMediaResponse

        override suspend fun confirmMedia(mediaId: UUID): Result<MediaFileResponseDto> {
            confirmedMediaIds += mediaId
            return Result.success(
                createMediaResponse.first { it.id == mediaId }.copy(
                    url = when (mediaId) {
                        MEDIA_ID_A -> "https://example.com/a-confirmed.png"
                        MEDIA_ID_B -> "https://example.com/b-confirmed.png"
                        else -> "https://example.com/confirmed.png"
                    },
                    status = "COMPLETED",
                )
            )
        }
    }

    private class FakeMediaUploadRemoteDataSource : MediaUploadRemoteDataSource {
        val uploadedUrls = mutableListOf<String>()

        override suspend fun uploadFile(
            url: String,
            contentType: String,
            bytes: ByteArray,
        ) {
            uploadedUrls += url
        }
    }

    private companion object {
        val REFERENCE_ID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val CORRELATION_ID_A: UUID = UUID.fromString("22222222-2222-2222-2222-222222222222")
        val CORRELATION_ID_B: UUID = UUID.fromString("33333333-3333-3333-3333-333333333333")
        val MEDIA_ID_A: UUID = UUID.fromString("44444444-4444-4444-4444-444444444444")
        val MEDIA_ID_B: UUID = UUID.fromString("55555555-5555-5555-5555-555555555555")

        fun mediaUploadRequest(
            correlationId: UUID,
            name: String = "upload.png",
            bytes: ByteArray = byteArrayOf(1, 2, 3),
        ): MediaUploadRequest {
            return MediaUploadRequest(
                correlationId = correlationId,
                referenceId = REFERENCE_ID,
                contentType = "image/png",
                name = name,
                altText = "Upload",
                bytes = bytes,
                isPublic = true,
            )
        }

        fun mediaFileResponseDto(
            id: UUID,
            correlationId: UUID,
            name: String = "server.png",
            url: String = "https://example.com/upload.png",
        ): MediaFileResponseDto {
            return MediaFileResponseDto(
                id = id,
                correlationId = correlationId,
                contentType = "image/png",
                provider = "S3",
                status = "PENDING",
                name = name,
                altText = "Upload",
                url = url,
                expiresAt = Instant.parse("2026-05-28T22:18:59.383200812Z"),
                isPublic = true,
            )
        }

        fun mediaEntity(
            url: String,
            urlExpireAt: Instant?,
        ): MediaEntity {
            return listOf(
                mediaFileResponseDto(
                    id = MEDIA_ID_A,
                    correlationId = CORRELATION_ID_A,
                    url = url,
                )
            ).toDomain(
                referenceId = REFERENCE_ID,
                referenceType = MediaReferenceType.PROFILE,
            ).single().toEntity()
                .copy(urlExpireAt = urlExpireAt)
        }
    }
}

