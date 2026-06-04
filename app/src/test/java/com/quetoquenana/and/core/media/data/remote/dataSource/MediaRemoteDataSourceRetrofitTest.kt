package com.quetoquenana.and.core.media.data.remote.dataSource

import com.quetoquenana.and.core.media.data.remote.api.MediaApi
import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.network.ApiResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.util.UUID

class MediaRemoteDataSourceRetrofitTest {

    @Test
    fun `getMedia uses backend mediaName for path parameter`() = runTest {
        val mediaApi = mockk<MediaApi>()
        val referenceId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        coEvery {
            mediaApi.getMedia(referenceType = "profile", id = referenceId)
        } returns ApiResponse(
            message = "Success",
            data = emptyList()
        )
        val dataSource = MediaRemoteDataSourceRetrofit(mediaApi = mediaApi)

        dataSource.getMedia(referenceId = referenceId, referenceType = MediaReferenceType.PROFILE)

        coVerify(exactly = 1) {
            mediaApi.getMedia(referenceType = "profile", id = referenceId)
        }
    }

    @Test
    fun `createMedia uses backend mediaName in request body`() = runTest {
        val mediaApi = mockk<MediaApi>()
        val referenceId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val request = MediaUploadRequest(
            correlationId = UUID.fromString("22222222-2222-2222-2222-222222222222"),
            referenceId = referenceId,
            contentType = "image/png",
            name = "profile.png",
            altText = "Profile image",
            bytes = byteArrayOf(1, 2, 3),
            isPublic = false,
        )
        coEvery {
            mediaApi.createMedia(
                id = referenceId,
                request = match { dto -> dto.referenceType == "profile" },
                applicationName = any(),
            )
        } returns ApiResponse(
            message = "Success",
            data = emptyList()
        )
        val dataSource = MediaRemoteDataSourceRetrofit(mediaApi = mediaApi)

        dataSource.createMedia(
            referenceId = referenceId,
            referenceType = MediaReferenceType.PROFILE,
            uploads = listOf(request)
        )

        coVerify(exactly = 1) {
            mediaApi.createMedia(
                id = referenceId,
                request = match { dto -> dto.referenceType == "profile" },
                applicationName = any(),
            )
        }
    }

    @Test
    fun `confirmMedia converts successful response body into result success`() = runTest {
        val mediaApi = mockk<MediaApi>()
        val mediaId = UUID.fromString("33333333-3333-3333-3333-333333333333")
        val expected = MediaFileResponseDto(
            id = mediaId,
            correlationId = UUID.fromString("44444444-4444-4444-4444-444444444444"),
            contentType = "image/png",
            provider = "S3",
            status = "COMPLETED",
            name = "profile.png",
            altText = "Profile image",
            url = "https://example.com/profile.png",
            expiresAt = Instant.parse("2026-05-28T22:18:59.383200812Z"),
            isPublic = true,
        )
        coEvery { mediaApi.confirmMedia(id = mediaId) } returns ApiResponse(
            message = "Success",
            data = expected,
        )
        val dataSource = MediaRemoteDataSourceRetrofit(mediaApi = mediaApi)

        val result = dataSource.confirmMedia(mediaId = mediaId)

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
        coVerify(exactly = 1) { mediaApi.confirmMedia(id = mediaId) }
    }
}

