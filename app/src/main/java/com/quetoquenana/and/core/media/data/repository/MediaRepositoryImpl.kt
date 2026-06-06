package com.quetoquenana.and.core.media.data.repository

import com.quetoquenana.and.core.media.data.local.datasource.MediaLocalDataSource
import com.quetoquenana.and.core.media.data.local.entity.MediaEntity
import com.quetoquenana.and.core.media.data.local.entity.requiresRefresh
import com.quetoquenana.and.core.media.data.local.entity.toDomain
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaRemoteDataSource
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaUploadRemoteDataSource
import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.domain.model.takeMatchingMedia
import com.quetoquenana.and.core.media.domain.model.toDomain
import com.quetoquenana.and.core.media.domain.model.toEntity
import com.quetoquenana.and.core.media.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.UUID
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val local: MediaLocalDataSource,
    private val remote: MediaRemoteDataSource,
    private val uploadRemote: MediaUploadRemoteDataSource,
) : MediaRepository {

    override fun observeMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType
    ): Flow<List<MediaAsset>> {
        return observeMediaEntities(
            referenceId = referenceId,
            referenceType = referenceType
        )
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun refreshMedia(
        referenceId: UUID,
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
        referenceId: UUID,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>,
    ) {
        val confirmedRemoteMedia = mutableListOf<MediaFileResponseDto>()

        // Generate urls to upload files
        val remoteMedia = getRemoteMediaUrl(
            referenceId = referenceId,
            referenceType = referenceType,
            uploads = uploads,
        ).toMutableList()

        // Upload files using generated urls
        uploads.forEach { upload ->
            // Find the matched media with correlationId and remove it from the list to prevent duplicate match for next uploads.
            val matchedRemoteMedia = remoteMedia.takeMatchingMedia(upload) ?: return@forEach

            // If the url is blank, continue to next upload without uploading.
            val uploadUrl = matchedRemoteMedia.url.takeUnless { it.isBlank() } ?: return@forEach

            // This is the actual file upload to the url provided by the backend
            uploadRemote.uploadFile(
                url = uploadUrl,
                contentType = upload.contentType,
                bytes = upload.bytes,
            )

            // Confirm upload after successful upload
            confirmedRemoteMedia += remote.confirmMedia(mediaId = matchedRemoteMedia.id)
        }

        if (confirmedRemoteMedia.isNotEmpty()) {
            local.saveAllMedia(
                confirmedRemoteMedia
                    .toDomain(
                        referenceId = referenceId,
                        referenceType = referenceType,
                    )
                    .map { it.toEntity() }
            )
        }
    }

    override suspend fun replaceMedia(
        referenceId: UUID,
        referenceType: MediaReferenceType,
        media: MediaUploadRequest
    ) {
        // Generate url to upload file
        val response = getRemoteMediaUrl(
            referenceId = referenceId,
            referenceType = referenceType,
            uploads = listOf(media),
        )
        val remoteMedia = response.firstOrNull() ?: return

        // This is the actual file upload to the url provided by the backend
        uploadRemote.uploadFile(
            url = remoteMedia.url,
            contentType = media.contentType,
            bytes = media.bytes,
        )
        val confirmed = remote.confirmMedia(mediaId = remoteMedia.id)
            .toDomain(
                referenceId = referenceId,
                referenceType = referenceType
            )

        // Confirm upload after successful upload
        local.updateMedia(confirmed.toEntity())
    }

    private suspend fun getRemoteMediaUrl(
        referenceId: UUID,
        referenceType: MediaReferenceType,
        uploads: List<MediaUploadRequest>,
    ): List<MediaFileResponseDto> {
        // Generate urls to upload files
        return remote.createMedia(
            referenceId = referenceId,
            referenceType = referenceType,
            uploads = uploads,
        ).toMutableList()
    }

    private fun observeMediaEntities(
        referenceId: UUID,
        referenceType: MediaReferenceType
    ): Flow<List<MediaEntity>> {
        return local.observeMedia(
            referenceId = referenceId,
            referenceType = referenceType.name,
        )
            .onStart {
                // If media requires refresh, fetch from remote and update local cache
                // before emitting the data to UI.
                val cachedMedia = local.getMedia(
                    referenceId = referenceId,
                    referenceType = referenceType.name,
                )

                if (cachedMedia.requiresRefresh()) {
                    refreshMedia(
                        referenceId = referenceId,
                        referenceType = referenceType,
                    )
                }
            }
    }

}


