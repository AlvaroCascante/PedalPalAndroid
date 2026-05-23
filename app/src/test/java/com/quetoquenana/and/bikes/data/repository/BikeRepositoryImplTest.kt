package com.quetoquenana.and.bikes.data.repository

import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.domain.repository.MediaRepository
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeComponentLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.entity.ComponentEntity
import com.quetoquenana.and.features.bikes.data.local.entity.BikeEntity
import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeRemoteDataSource
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeComponentDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeHistoryDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectionStatusDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaBikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectUrlDto
import com.quetoquenana.and.features.bikes.data.remote.dto.ComponentDto
import com.quetoquenana.and.features.bikes.data.repository.BikeRepositoryImpl
import com.quetoquenana.and.features.bikes.domain.model.AddComponentRequest
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
            mediaRepository = FakeMediaRepository()
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
            ),
            mediaRepository = FakeMediaRepository(
                media = listOf(
                    media(id = "img-1", contentType = "IMAGE_PNG", name = "First image"),
                    media(id = "video-1", contentType = "VIDEO_MP4", name = "Ride clip"),
                    media(id = "img-2", contentType = "IMAGE_JPEG", name = "Second image")
                )
            ),
        )

        val result = repository.getBikeMedia(id = "bike-1")

        assertEquals(listOf("img-1", "img-2"), result.map { it.id })
        assertEquals(listOf("First image", "Second image"), result.map { it.name })
    }

    @Test
    fun `uploadBikeMedia creates uploads and confirms each selected image`() = runTest {
        val mediaRepository = FakeMediaRepository(
            media = listOf(
                media(id = "media-2", contentType = "IMAGE_PNG", name = "SecondBikeImage.png"),
                media(id = "media-1", contentType = "IMAGE_PNG", name = "FirstBikeImage.png")
            )
        )
        val repository = BikeRepositoryImpl(
            local = FakeBikeLocalDataSource(),
            componentLocal = FakeBikeComponentLocalDataSource(),
            remote = FakeBikeRemoteDataSource(componentTypes = emptySet()),
            mediaRepository = mediaRepository
        )

        repository.uploadBikeMedia(
            bikeId = "bike-1",
            uploads = listOf(
                MediaUploadRequest(
                    name = "FirstBikeImage.png",
                    altText = "FirstBikeImage.png",
                    contentType = "image/png",
                    bytes = byteArrayOf(1),
                    isPublic = false,
                ),
                MediaUploadRequest(
                    name = "SecondBikeImage.png",
                    altText = "SecondBikeImage.png",
                    contentType = "image/png",
                    bytes = byteArrayOf(2),
                    isPublic = false,
                )
            )
        )

        assertEquals(
            listOf("FirstBikeImage.png", "SecondBikeImage.png"),
            mediaRepository.uploadRequests.flatten().map { it.name }
        )
        assertEquals("bike-1", mediaRepository.lastUploadReferenceId)
        assertEquals(MediaReferenceType.BIKE, mediaRepository.lastUploadReferenceType)
    }

    @Test
    fun `uploadBikeProfileImage stores media with bike profile reference type`() = runTest {
        val mediaRepository = FakeMediaRepository()
        val repository = BikeRepositoryImpl(
            local = FakeBikeLocalDataSource(),
            componentLocal = FakeBikeComponentLocalDataSource(),
            remote = FakeBikeRemoteDataSource(componentTypes = emptySet()),
            mediaRepository = mediaRepository,
        )

        repository.uploadBikeProfileImage(
            bikeId = "bike-1",
            upload = MediaUploadRequest(
                name = "BikeProfile.png",
                altText = "Bike profile",
                contentType = "image/png",
                bytes = byteArrayOf(9),
                isPublic = false,
            ),
        )

        assertEquals("bike-1", mediaRepository.lastUploadReferenceId)
        assertEquals(MediaReferenceType.BIKE_PROFILE, mediaRepository.lastUploadReferenceType)
    }

    @Test
    fun `getBikeProfileImageUrl returns first available bike profile image`() = runTest {
        val repository = BikeRepositoryImpl(
            local = FakeBikeLocalDataSource(),
            componentLocal = FakeBikeComponentLocalDataSource(),
            remote = FakeBikeRemoteDataSource(componentTypes = emptySet()),
            mediaRepository = FakeMediaRepository(
                media = listOf(
                    media(
                        id = "profile-1",
                        contentType = "IMAGE_PNG",
                        name = "Profile image",
                        referenceType = MediaReferenceType.BIKE_PROFILE,
                    )
                )
            ),
        )

        val result = repository.getBikeProfileImageUrl(id = "bike-1")

        assertEquals("https://example.com/profile-1", result)
    }

    @Test
    fun `getBikes on cold cache saves bikes before components and returns synced data`() = runTest {
        val local = StatefulBikeLocalDataSource()
        val componentLocal = ForeignKeyCheckingBikeComponentLocalDataSource {
            bikeId -> local.containsBike(bikeId)
        }
        val repository = BikeRepositoryImpl(
            local = local,
            componentLocal = componentLocal,
            remote = FakeBikeRemoteDataSource(
                componentTypes = emptySet(),
                bikes = listOf(
                    bikeDto(
                        id = "bike-1",
                        componentIds = listOf("component-1")
                    )
                )
            ),
            mediaRepository = FakeMediaRepository(),
        )

        val result = repository.getBikes(refresh = false)

        assertEquals(listOf("bike-1"), result.map { it.id })
        assertEquals(listOf("component-1"), result.single().components.map { it.id })
    }

    private class FakeBikeLocalDataSource : BikeLocalDataSource {
        override fun observeBikes(): Flow<List<BikeEntity>> {
            return flowOf(emptyList())
        }

        override suspend fun hasActiveBikes(): Boolean {
            return false
        }

        override suspend fun getBikes(): List<BikeEntity> {
            return emptyList()
        }

        override suspend fun getBikeById(id: String): BikeEntity? {
            return null
        }

        override suspend fun saveBike(bike: BikeEntity) {}

        override suspend fun saveBikes(bikes: List<BikeEntity>) {}

        override suspend fun clearBikes() {}
    }

    private class StatefulBikeLocalDataSource : BikeLocalDataSource {
        private val bikes = mutableListOf<BikeEntity>()

        fun containsBike(id: String): Boolean = bikes.any { it.id == id }

        override fun observeBikes(): Flow<List<BikeEntity>> = flowOf(bikes.toList())

        override suspend fun hasActiveBikes(): Boolean = bikes.any { it.status.equals("ACTIVE", ignoreCase = true) }

        override suspend fun getBikes(): List<BikeEntity> = bikes.toList()

        override suspend fun getBikeById(id: String): BikeEntity? = bikes.firstOrNull { it.id == id }

        override suspend fun saveBike(bike: BikeEntity) {
            bikes.removeAll { it.id == bike.id }
            bikes += bike
        }

        override suspend fun saveBikes(bikes: List<BikeEntity>) {
            this.bikes.removeAll { existing -> bikes.any { it.id == existing.id } }
            this.bikes += bikes
        }

        override suspend fun clearBikes() {
            bikes.clear()
        }
    }

    private class FakeBikeComponentLocalDataSource : BikeComponentLocalDataSource {
        override suspend fun getComponentsForBike(bikeId: String): List<ComponentEntity> = emptyList()

        override suspend fun saveComponent(component: ComponentEntity) = Unit

        override suspend fun saveComponents(components: List<ComponentEntity>) = Unit

        override suspend fun clearComponentsForBike(bikeId: String) = Unit

        override suspend fun clearComponents() = Unit
    }

    private class ForeignKeyCheckingBikeComponentLocalDataSource(
        private val bikeExists: (String) -> Boolean
    ) : BikeComponentLocalDataSource {
        private val componentsByBikeId = mutableMapOf<String, MutableList<ComponentEntity>>()

        override suspend fun getComponentsForBike(bikeId: String): List<ComponentEntity> {
            return componentsByBikeId[bikeId].orEmpty().toList()
        }

        override suspend fun saveComponent(component: ComponentEntity) {
            saveComponents(listOf(component))
        }

        override suspend fun saveComponents(components: List<ComponentEntity>) {
            components.forEach { component ->
                check(bikeExists(component.bikeId)) {
                    "Bike ${component.bikeId} must exist before saving components"
                }
            }
            components.forEach { component ->
                val stored = componentsByBikeId.getOrPut(component.bikeId) { mutableListOf() }
                stored.removeAll { it.id == component.id }
                stored += component
            }
        }

        override suspend fun clearComponentsForBike(bikeId: String) {
            componentsByBikeId.remove(bikeId)
        }

        override suspend fun clearComponents() {
            componentsByBikeId.clear()
        }
    }

    private class FakeBikeRemoteDataSource(
        private val componentTypes: Set<ComponentDto>,
        private val bikes: List<BikeDto> = emptyList(),
    ) : BikeRemoteDataSource {
        override suspend fun getBikeComponentTypes(): Set<ComponentDto> = componentTypes

        override suspend fun getBikes(): List<BikeDto> = bikes

        override suspend fun getBike(id: String): BikeDto = error("Not needed")

        override suspend fun getBikeHistory(id: String): List<BikeHistoryDto> = emptyList()


        override suspend fun createBike(request: CreateBikeRequest): BikeDto = error("Not needed")

        override suspend fun addBikeComponent(
            bikeId: String,
            request: AddComponentRequest
        ): BikeComponentDto = error("Not needed")

        override suspend fun getStravaConnectUrl(): StravaConnectUrlDto = error("Not needed")

        override suspend fun getStravaConnectionStatus(): StravaConnectionStatusDto = error("Not needed")

        override suspend fun getStravaBikes(): List<StravaBikeDto> = emptyList()
    }

    private class FakeMediaRepository(
        initialMedia: List<MediaAsset> = emptyList(),
        media: List<MediaAsset> = initialMedia,
    ) : MediaRepository {
        private val storedMedia = media.toMutableList()
        val uploadRequests = mutableListOf<List<MediaUploadRequest>>()
        var lastUploadReferenceId: String? = null
        var lastUploadReferenceType: MediaReferenceType? = null

        override fun observeMedia(
            referenceId: String,
            referenceType: MediaReferenceType,
            refresh: Boolean
        ): Flow<List<MediaAsset>> {
            return flowOf(storedMedia.filter { it.referenceId == referenceId && it.referenceType == referenceType })
        }

        override fun observePrimaryMedia(
            referenceId: String,
            referenceType: MediaReferenceType,
            refresh: Boolean
        ): Flow<MediaAsset?> {
            return flowOf(
                storedMedia.firstOrNull { it.referenceId == referenceId && it.referenceType == referenceType }
            )
        }

        override suspend fun refreshMedia(
            referenceId: String,
            referenceType: MediaReferenceType,
        ) = Unit

        override suspend fun uploadMedia(
            referenceId: String,
            referenceType: MediaReferenceType,
            uploads: List<MediaUploadRequest>
        ) {
            uploadRequests += uploads
            lastUploadReferenceId = referenceId
            lastUploadReferenceType = referenceType
            val created = uploads.mapIndexed { index, upload ->
                MediaAsset(
                    referenceId = referenceId,
                    referenceType = referenceType,
                    mediaId = "created-${storedMedia.size + index + 1}",
                    url = "https://example.com/${upload.name}-${index + 1}",
                    contentType = upload.contentType.replace("image/", "IMAGE_").uppercase(),
                    name = upload.name,
                    altText = upload.altText,
                    isPrivate = true,
                    urlExpireAt = null,
                    updatedAt = 0L,
                    fetchedAt = 0L,
                )
            }
            storedMedia += created
        }
    }

    private companion object {
        fun media(
            id: String,
            contentType: String,
            name: String,
            referenceType: MediaReferenceType = MediaReferenceType.BIKE,
        ): MediaAsset {
            return MediaAsset(
                referenceId = "bike-1",
                referenceType = referenceType,
                mediaId = id,
                url = "https://example.com/$id",
                contentType = contentType,
                name = name,
                altText = "$name alt",
                isPrivate = true,
                urlExpireAt = null,
                updatedAt = 0L,
                fetchedAt = 0L,
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

        fun bikeDto(
            id: String,
            componentIds: List<String> = emptyList()
        ): BikeDto {
            return BikeDto(
                id = id,
                name = "Bike $id",
                type = "ROAD",
                status = "Active",
                isPublic = false,
                isExternalSync = false,
                brand = null,
                model = null,
                year = null,
                serialNumber = null,
                notes = null,
                odometerKm = 0.0,
                usageTimeMinutes = 0,
                externalGearId = null,
                externalSyncProvider = "UNKNOWN",
                components = componentIds.map { componentId ->
                    BikeComponentDto(
                        id = componentId,
                        type = "Pedals",
                        name = "Component $componentId",
                        status = "Active",
                        brand = null,
                        model = null,
                        notes = null,
                        odometerKm = 0,
                        usageTimeMinutes = 0
                    )
                }.toSet()
            )
        }
    }
}
