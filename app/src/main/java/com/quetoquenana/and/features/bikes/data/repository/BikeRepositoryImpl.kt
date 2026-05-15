package com.quetoquenana.and.features.bikes.data.repository

import com.quetoquenana.and.features.bikes.data.local.datasource.BikeComponentLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.entity.toDomain as componentEntityToDomain
import com.quetoquenana.and.features.bikes.data.local.entity.toEntity as componentToEntity
import com.quetoquenana.and.features.bikes.data.local.entity.toDomain
import com.quetoquenana.and.features.bikes.data.local.entity.toEntity
import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeMediaUploadRemoteDataSource
import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeRemoteDataSource
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeMediaDto
import com.quetoquenana.and.features.bikes.data.remote.dto.toDomain
import com.quetoquenana.and.features.bikes.domain.model.AddBikeComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeComponent
import com.quetoquenana.and.features.bikes.domain.model.BikeComponentType
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import com.quetoquenana.and.features.bikes.domain.model.BikeMediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BikeRepositoryImpl @Inject constructor(
    private val local: BikeLocalDataSource,
    private val componentLocal: BikeComponentLocalDataSource,
    private val remote: BikeRemoteDataSource,
    private val mediaUploadRemote: BikeMediaUploadRemoteDataSource
) : BikeRepository {

    override suspend fun getBikeComponentTypes(): List<BikeComponentType> {
        return remote.getBikeComponentTypes()
            .map { it.toDomain() }
            .sortedWith(
                compareBy<BikeComponentType> { it.position ?: Int.MAX_VALUE }
                    .thenBy { it.codeDescription }
            )
    }

    override fun observeBikes(): Flow<List<Bike>> {
        return local.observeBikes().map { entities ->
            entities.map { entity ->
                val bike = entity.toDomain()
                bike.copy(
                    components = componentLocal.getComponentsForBike(bike.id)
                        .map { it.componentEntityToDomain() }
                )
            }
        }
    }

    override suspend fun getBikes(refresh: Boolean  ): List<Bike> {
        if (refresh || local.getBikes().isEmpty()) {
            val bikes = remote.getBikes().map { it.toDomain() }
            val now = System.currentTimeMillis()
            local.saveBikes(bikes.map { it.toEntity(currentTimeMillis = now) })
            bikes.forEach { bike ->
                componentLocal.clearComponentsForBike(bikeId = bike.id)
                componentLocal.saveComponents(
                    bike.components.map { component ->
                        component.componentToEntity(
                            bikeId = bike.id,
                            currentTimeMillis = now
                        )
                    }
                )
            }
        }
        val localBikes = local.getBikes().map { entity ->
            val bike = entity.toDomain()
            bike.copy(
                components = componentLocal.getComponentsForBike(bike.id)
                    .map { it.componentEntityToDomain() }
            )
        }

        return localBikes
    }

    override suspend fun getBike(id: String): Bike {
        val bike = remote.getBike(id = id).toDomain()
        val now = System.currentTimeMillis()
        local.saveBike(bike.toEntity(currentTimeMillis = now))
        componentLocal.clearComponentsForBike(bikeId = bike.id)
        componentLocal.saveComponents(
            bike.components.map { component ->
                component.componentToEntity(
                    bikeId = bike.id,
                    currentTimeMillis = now
                )
            }
        )
        return bike
    }

    override suspend fun getBikeHistory(id: String): List<BikeHistory> {
        return remote.getBikeHistory(id = id).map { it.toDomain() }
    }

    override suspend fun getBikeMedia(id: String): List<BikeMedia> {
        return remote.getBikeMedia(id = id).toDomain()
    }

    override suspend fun uploadBikeMedia(
        bikeId: String,
        uploads: List<BikeMediaUploadRequest>
    ) {
        if (uploads.isEmpty()) return

        val createdMedia = remote.createBikeMedia(
            bikeId = bikeId,
            uploads = uploads
        )
        val remainingCreatedMedia = createdMedia.mediaUrlResponse.toMutableList()

        uploads.forEach { upload ->
            val remoteMedia = remainingCreatedMedia.takeMatchingMedia(upload)
                ?: throw IllegalStateException("Unable to match uploaded image with server media response")

            mediaUploadRemote.uploadFile(
                url = remoteMedia.url,
                contentType = upload.contentType,
                bytes = upload.bytes
            )
            remote.confirmBikeMedia(mediaId = remoteMedia.id)
        }
    }

    override suspend fun createBike(request: CreateBikeRequest): Bike {
        val bike = remote.createBike(request).toDomain()
        local.saveBike(bike.toEntity(currentTimeMillis = System.currentTimeMillis()))
        return bike
    }

    override suspend fun addBikeComponent(
        bikeId: String,
        request: AddBikeComponentRequest
    ): BikeComponent {
        val component = remote.addBikeComponent(
            bikeId = bikeId,
            request = request
        ).toDomain()
        componentLocal.saveComponent(
            component.componentToEntity(
                bikeId = bikeId,
                currentTimeMillis = System.currentTimeMillis()
            )
        )
        return component
    }

    override suspend fun getStravaConnectUrl(): StravaConnectUrl {
        return remote.getStravaConnectUrl().toDomain()
    }

    override suspend fun getStravaBikes(): List<StravaBike> {
        return remote.getStravaBikes().map { it.toDomain() }
    }
}

private fun MutableList<BikeMediaDto>.takeMatchingMedia(
    upload: BikeMediaUploadRequest
): BikeMediaDto? {
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

