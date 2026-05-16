package com.quetoquenana.and.bikes.data.repository

import com.quetoquenana.and.features.bikes.data.local.datasource.BikeComponentLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.entity.BikeComponentEntity
import com.quetoquenana.and.features.bikes.data.local.entity.BikeEntity
import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeMediaUploadRemoteDataSource
import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeRemoteDataSource
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeComponentDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeHistoryDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeMediaDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeMediaResponseDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaBikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectUrlDto
import com.quetoquenana.and.features.bikes.data.remote.dto.ComponentDto
import com.quetoquenana.and.features.bikes.data.repository.BikeRepositoryImpl
import com.quetoquenana.and.features.bikes.domain.model.AddBikeComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.BikeMediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BikeRepositoryImplTest {

    @Test
    fun `getBikeComponentTypes maps remote system codes ordered by position`() = runTest {
        val repository = BikeRepositoryImpl(
            local = FakeBikeLocalDataSource(),
            componentLocal = FakeBikeComponentLocalDataSource(),
            remote = FakeBikeRemoteDataSource(
                componentTypes = setOf(
                    systemCode(code = "CASSETTE", description = "Cassette", position = 20),
                    systemCode(code = "CHAIN", description = "Chain", position = 10),
                    systemCode(code = "OTHER", description = "Other", position = null)
                )
            ),
            mediaUploadRemote = FakeBikeMediaUploadRemoteDataSource()
        )

        val result = repository.getBikeComponentTypes()

        assertEquals(listOf("CHAIN", "CASSETTE", "OTHER"), result.map { it.code })
        assertEquals(listOf("Chain", "Cassette", "Other"), result.map { it.codeDescription })
    }

    @Test
    fun `getBikeMedia preserves backend image order and filters non image content`() = runTest {
        val repository = BikeRepositoryImpl(
            local = FakeBikeLocalDataSource(),
            componentLocal = FakeBikeComponentLocalDataSource(),
            remote = FakeBikeRemoteDataSource(
                componentTypes = emptySet(),
                bikeMedia = BikeMediaResponseDto(
                    id = "bike-1",
                    mediaUrlResponse = listOf(
                        bikeMedia(id = "img-1", contentType = "IMAGE_PNG", name = "First image"),
                        bikeMedia(id = "video-1", contentType = "VIDEO_MP4", name = "Ride clip"),
                        bikeMedia(id = "img-2", contentType = "IMAGE_JPEG", name = "Second image")
                    )
                )
            ),
            mediaUploadRemote = FakeBikeMediaUploadRemoteDataSource()
        )

        val result = repository.getBikeMedia(id = "bike-1")

        assertEquals(listOf("img-1", "img-2"), result.map { it.id })
        assertEquals(listOf("First image", "Second image"), result.map { it.name })
    }

    @Test
    fun `uploadBikeMedia creates uploads and confirms each selected image`() = runTest {
        val remote = FakeBikeRemoteDataSource(
            componentTypes = emptySet(),
            bikeMedia = BikeMediaResponseDto(
                id = "bike-1",
                mediaUrlResponse = listOf(
                    bikeMedia(id = "media-2", contentType = "IMAGE_PNG", name = "SecondBikeImage.png"),
                    bikeMedia(id = "media-1", contentType = "IMAGE_PNG", name = "FirstBikeImage.png")
                )
            )
        )
        val uploadRemote = FakeBikeMediaUploadRemoteDataSource()
        val repository = BikeRepositoryImpl(
            local = FakeBikeLocalDataSource(),
            componentLocal = FakeBikeComponentLocalDataSource(),
            remote = remote,
            mediaUploadRemote = uploadRemote
        )

        repository.uploadBikeMedia(
            bikeId = "bike-1",
            uploads = listOf(
                BikeMediaUploadRequest(
                    name = "FirstBikeImage.png",
                    altText = "FirstBikeImage.png",
                    contentType = "image/png",
                    isPrimary = true,
                    bytes = byteArrayOf(1)
                ),
                BikeMediaUploadRequest(
                    name = "SecondBikeImage.png",
                    altText = "SecondBikeImage.png",
                    contentType = "image/png",
                    isPrimary = false,
                    bytes = byteArrayOf(2)
                )
            )
        )

        assertEquals(listOf("FirstBikeImage.png", "SecondBikeImage.png"), remote.createdUploadRequests.flatten().map { it.name })
        assertEquals(
            listOf(
                "https://example.com/media-1",
                "https://example.com/media-2"
            ),
            uploadRemote.uploadedUrls
        )
        assertEquals(listOf("media-1", "media-2"), remote.confirmedMediaIds)
    }

    private class FakeBikeLocalDataSource : BikeLocalDataSource {
        override fun observeBikes(): Flow<List<BikeEntity>> = flowOf(emptyList())

        override suspend fun getBikes(): List<BikeEntity> = emptyList()

        override suspend fun saveBike(bike: BikeEntity) = Unit

        override suspend fun saveBikes(bikes: List<BikeEntity>) = Unit

        override suspend fun clearBikes() = Unit
    }

    private class FakeBikeComponentLocalDataSource : BikeComponentLocalDataSource {
        override suspend fun getComponentsForBike(bikeId: String): List<BikeComponentEntity> = emptyList()

        override suspend fun saveComponent(component: BikeComponentEntity) = Unit

        override suspend fun saveComponents(components: List<BikeComponentEntity>) = Unit

        override suspend fun clearComponentsForBike(bikeId: String) = Unit

        override suspend fun clearComponents() = Unit
    }

    private class FakeBikeRemoteDataSource(
        private val componentTypes: Set<ComponentDto>,
        private val bikeMedia: BikeMediaResponseDto = BikeMediaResponseDto(id = "bike-1")
    ) : BikeRemoteDataSource {
        val createdUploadRequests = mutableListOf<List<BikeMediaUploadRequest>>()
        val confirmedMediaIds = mutableListOf<String>()

        override suspend fun getBikeComponentTypes(): Set<ComponentDto> = componentTypes

        override suspend fun getBikes(): List<BikeDto> = emptyList()

        override suspend fun getBike(id: String): BikeDto = error("Not needed")

        override suspend fun getBikeHistory(id: String): List<BikeHistoryDto> = emptyList()

        override suspend fun getBikeMedia(id: String): BikeMediaResponseDto = bikeMedia

        override suspend fun createBikeMedia(
            bikeId: String,
            uploads: List<BikeMediaUploadRequest>
        ): BikeMediaResponseDto {
            createdUploadRequests += uploads
            return bikeMedia
        }

        override suspend fun confirmBikeMedia(mediaId: String) {
            confirmedMediaIds += mediaId
        }

        override suspend fun createBike(request: CreateBikeRequest): BikeDto = error("Not needed")

        override suspend fun addBikeComponent(
            bikeId: String,
            request: AddBikeComponentRequest
        ): BikeComponentDto = error("Not needed")

        override suspend fun getStravaConnectUrl(): StravaConnectUrlDto = error("Not needed")

        override suspend fun getStravaBikes(): List<StravaBikeDto> = emptyList()
    }

    private class FakeBikeMediaUploadRemoteDataSource : BikeMediaUploadRemoteDataSource {
        val uploadedUrls = mutableListOf<String>()

        override suspend fun uploadFile(url: String, contentType: String, bytes: ByteArray) {
            uploadedUrls += url
        }
    }

    private companion object {
        fun bikeMedia(
            id: String,
            contentType: String,
            name: String
        ): BikeMediaDto {
            return BikeMediaDto(
                id = id,
                contentType = contentType,
                provider = "Cloudflare",
                isPrimary = false,
                status = "Active",
                name = name,
                altText = "$name alt",
                url = "https://example.com/$id",
                expiresAt = "2026-05-15T03:28:49Z"
            )
        }

        fun systemCode(
            code: String,
            description: String,
            position: Int?
        ): ComponentDto {
            return ComponentDto(
                id = "id-$code",
                category = "BIKE_COMPONENT_TYPE",
                code = code,
                codeDescription = description,
                status = "ACTIVE",
                position = position
            )
        }
    }
}
