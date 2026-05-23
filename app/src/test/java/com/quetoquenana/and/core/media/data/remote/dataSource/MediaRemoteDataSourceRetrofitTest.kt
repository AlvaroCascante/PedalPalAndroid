package com.quetoquenana.and.core.media.data.remote.dataSource

import com.quetoquenana.and.core.media.data.remote.api.MediaApi
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.network.ApiResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MediaRemoteDataSourceRetrofitTest {

    @Test
    fun `getMedia uses backend mediaName for path parameter`() = runTest {
        val mediaApi = mockk<MediaApi>()
        coEvery {
            mediaApi.getMedia(referenceType = "profile", id = "user-1")
        } returns ApiResponse(
            message = "Success",
            data = emptyList()
        )
        val dataSource = MediaRemoteDataSourceRetrofit(mediaApi = mediaApi)

        dataSource.getMedia(referenceId = "user-1", referenceType = MediaReferenceType.PROFILE)

        coVerify(exactly = 1) {
            mediaApi.getMedia(referenceType = "profile", id = "user-1")
        }
    }

    @Test
    fun `createMedia uses backend mediaName in request body`() = runTest {
        val mediaApi = mockk<MediaApi>()
        val request = MediaUploadRequest(
            name = "profile.png",
            altText = "Profile image",
            contentType = "image/png",
            bytes = byteArrayOf(1, 2, 3),
            isPublic = false,
        )
        coEvery {
            mediaApi.createMedia(
                id = "user-1",
                request = match { dto -> dto.referenceType == "profile" },
                applicationName = any(),
            )
        } returns ApiResponse(
            message = "Success",
            data = emptyList()
        )
        val dataSource = MediaRemoteDataSourceRetrofit(mediaApi = mediaApi)

        dataSource.createMedia(
            referenceId = "user-1",
            referenceType = MediaReferenceType.PROFILE,
            uploads = listOf(request)
        )

        coVerify(exactly = 1) {
            mediaApi.createMedia(
                id = "user-1",
                request = match { dto -> dto.referenceType == "profile" },
                applicationName = any(),
            )
        }
    }
}

