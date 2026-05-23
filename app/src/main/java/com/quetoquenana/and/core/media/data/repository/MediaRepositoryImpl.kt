package com.quetoquenana.and.core.media.data.repository

import com.quetoquenana.and.core.media.data.local.datasource.MediaLocalDataSource
import com.quetoquenana.and.core.media.data.local.entity.toDomain
import com.quetoquenana.and.core.media.data.local.entity.toEntity
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaRemoteDataSource
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaUploadRemoteDataSource
import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.domain.model.primaryImage
import com.quetoquenana.and.core.media.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val local: MediaLocalDataSource,
    private val remote: MediaRemoteDataSource,
    private val uploadRemote: MediaUploadRemoteDataSource,
) : MediaRepository {

    override fun observeMedia(
        referenceId: String,
        referenceType: MediaReferenceType,
        refresh: Boolean,
    ): Flow<List<MediaAsset>> {
        return local.observeMedia(
            referenceId = referenceId,
            referenceType = referenceType.name,
        )
            .onStart {
                if (refresh) {
                    refreshMedia(
                        referenceId = referenceId,
                        referenceType = referenceType,
                    )
                }
            }
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override fun observePrimaryMedia(
        referenceId: String,
        referenceType: MediaReferenceType,
        refresh: Boolean,
    ): Flow<MediaAsset?> {

        return observeMedia(
            referenceId = referenceId,
            referenceType = referenceType,
            refresh = refresh,
        ).map { media ->
            media.primaryImage()
        }
    }

    override suspend fun refreshMedia(
        referenceId: String,
        referenceType: MediaReferenceType,
    ) {
        val remoteMedia = remote.getMedia(
            referenceId = referenceId,
            referenceType = referenceType,
        ).toDomain(
            referenceId = referenceId,
            referenceType = referenceType
        )
        local.saveAllMedia(remoteMedia.map { it.toEntity() })
    }

    override suspend fun uploadMedia(
        referenceId: String,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>,
    ) {

        // Generate urls to upload files
        val remoteMedia = remote.createMedia(
            referenceId = referenceId,
            referenceType = referenceType,
            uploads = uploads,
        ).toMutableList()

        // Upload files using generated urls
        uploads.forEach { upload ->
            val matchedRemoteMedia = remoteMedia.takeMatchingMedia(upload) ?: return@forEach
            val uploadUrl = matchedRemoteMedia.url.takeIf { it.isNotBlank() } ?: return@forEach

            uploadRemote.uploadFile(
                url = uploadUrl,
                contentType = upload.contentType,
                bytes = upload.bytes,
            )

            // Confirm upload after successful upload
            remote.confirmMedia(mediaId = matchedRemoteMedia.id)
        }

        // Refresh local media after uploads
        refreshMedia(
            referenceId = referenceId,
            referenceType = referenceType,
        )
    }
}

private fun List<MediaFileResponseDto>.toDomain(
    referenceId: String,
    referenceType: MediaReferenceType
): List<MediaAsset> {
    val currentTime = System.currentTimeMillis()
    return map { media ->
        MediaAsset(
            referenceId = referenceId,
            referenceType = referenceType,
            mediaId = media.id,
            url = media.url,
            contentType = media.contentType,
            name = media.name,
            altText = media.altText,
            isPrivate = !media.isPublic,
            urlExpireAt = media.expiresAt,
            updatedAt = currentTime,
            fetchedAt = currentTime
        )
    }
}

private fun MutableList<MediaFileResponseDto>.takeMatchingMedia(
    upload: MediaUploadRequest,
): MediaFileResponseDto? {
    val preferredIndex = indexOfFirst { it.name == upload.name && it.url.isNotBlank() }
    val fallbackIndex = indexOfFirst { it.url.isNotBlank() }
    val matchIndex = when {
        preferredIndex >= 0 -> preferredIndex
        fallbackIndex >= 0 -> fallbackIndex
        else -> -1
    }

    if (matchIndex < 0) return null
    return removeAt(matchIndex)
}

