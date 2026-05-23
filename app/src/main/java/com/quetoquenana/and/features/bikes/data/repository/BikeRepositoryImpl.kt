package com.quetoquenana.and.features.bikes.data.repository

import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.domain.model.primaryImage
import com.quetoquenana.and.core.media.domain.repository.MediaRepository
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeComponentLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.entity.ComponentEntity
import com.quetoquenana.and.features.bikes.data.local.entity.BikeEntity
import com.quetoquenana.and.features.bikes.data.local.entity.toDomain as componentEntityToDomain
import com.quetoquenana.and.features.bikes.data.local.entity.toEntity as componentToEntity
import com.quetoquenana.and.features.bikes.data.local.entity.toDomain
import com.quetoquenana.and.features.bikes.data.local.entity.toEntity
import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeRemoteDataSource
import com.quetoquenana.and.features.bikes.data.remote.dto.toDomain
import com.quetoquenana.and.features.bikes.domain.model.AddComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.Component
import com.quetoquenana.and.features.bikes.domain.model.ComponentType
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import com.quetoquenana.and.features.bikes.domain.model.toBikeMedia
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class BikeRepositoryImpl @Inject constructor(
    private val local: BikeLocalDataSource,
    private val componentLocal: BikeComponentLocalDataSource,
    private val remote: BikeRemoteDataSource,
    private val mediaRepository: MediaRepository
) : BikeRepository {

    override suspend fun getBikeComponentTypes(): List<ComponentType> {
        val componentTypes = mutableListOf<ComponentType>()
        for (dto in remote.getBikeComponentTypes()) {
            componentTypes += dto.toDomain()
        }
        return componentTypes.sortedWith(
            compareBy<ComponentType> { it.position ?: Int.MAX_VALUE }
                .thenBy { it.codeDescription }
        )
    }

    override suspend fun hasActiveBikesLocally(): Boolean {
        return local.hasActiveBikes()
    }

    override suspend fun getBikeProfileImageUrl(id: String): String? {
        return mediaRepository.observePrimaryMedia(
            referenceId = id,
            referenceType = MediaReferenceType.BIKE_PROFILE,
            refresh = false,
        ).first()?.url
    }

    override fun observeBikes(): Flow<List<Bike>> {
        return local.observeBikes().map { entities ->
            val bikes = mutableListOf<Bike>()
            for (entity in entities) {
                val bike = entity.toDomain()
                val components = mutableListOf<Component>()
                for (componentEntity in componentLocal.getComponentsForBike(bike.id)) {
                    components += componentEntity.componentEntityToDomain()
                }
                bikes += bike.copy(components = components)
            }
            bikes
        }
    }

    override suspend fun getBikes(refresh: Boolean): List<Bike> {
        if (refresh || local.getBikes().isEmpty()) {
            val bikes = mutableListOf<Bike>()
            for (dto in remote.getBikes()) {
                bikes += dto.toDomain()
            }
            val now = System.currentTimeMillis()
            val bikeEntities = mutableListOf<BikeEntity>()
            val componentEntitiesByBikeId = mutableMapOf<String, List<ComponentEntity>>()
            for (bike in bikes) {
                bikeEntities += bike.toEntity(currentTimeMillis = now)
                val componentEntities = mutableListOf<ComponentEntity>()
                for (component in bike.components) {
                    componentEntities += component.componentToEntity(
                        bikeId = bike.id,
                        currentTimeMillis = now
                    )
                }
                componentEntitiesByBikeId[bike.id] = componentEntities
            }
            local.saveBikes(bikeEntities)
            for ((bikeId, componentEntities) in componentEntitiesByBikeId) {
                componentLocal.clearComponentsForBike(bikeId = bikeId)
                componentLocal.saveComponents(componentEntities)
            }
        }

        val localBikes = mutableListOf<Bike>()
        for (entity in local.getBikes()) {
            val bike = entity.toDomain()
            val components = mutableListOf<Component>()
            for (componentEntity in componentLocal.getComponentsForBike(bike.id)) {
                components += componentEntity.componentEntityToDomain()
            }
            localBikes += bike.copy(components = components)
        }
        return localBikes
    }

    override suspend fun getBike(id: String): Bike {
        val bike = remote.getBike(id = id).toDomain()
        val now = System.currentTimeMillis()
        local.saveBike(bike.toEntity(currentTimeMillis = now))
        componentLocal.clearComponentsForBike(bikeId = bike.id)
        val componentEntities = mutableListOf<ComponentEntity>()
        for (component in bike.components) {
            componentEntities += component.componentToEntity(
                bikeId = bike.id,
                currentTimeMillis = now
            )
        }
        componentLocal.saveComponents(componentEntities)
        return bike
    }

    override suspend fun getBikeHistory(id: String): List<BikeHistory> {
        val history = mutableListOf<BikeHistory>()
        for (dto in remote.getBikeHistory(id = id)) {
            history += dto.toDomain()
        }
        return history
    }

    override suspend fun getBikeMedia(id: String): List<BikeMedia> {
        return mediaRepository.observeMedia(
            referenceId = id,
            referenceType = MediaReferenceType.BIKE,
            refresh = true
        ).first().toBikeMedia()
    }

    override suspend fun uploadBikeMedia(
        bikeId: String,
        uploads: List<MediaUploadRequest>
    ) {
        if (uploads.isEmpty()) return

        mediaRepository.uploadMedia(
            referenceId = bikeId,
            referenceType = MediaReferenceType.BIKE,
            uploads = uploads
        )
    }

    override suspend fun uploadBikeProfileImage(
        bikeId: String,
        upload: MediaUploadRequest
    ) {
        mediaRepository.uploadMedia(
            referenceId = bikeId,
            referenceType = MediaReferenceType.BIKE_PROFILE,
            uploads = listOf(upload)
        )
    }

    override suspend fun createBike(request: CreateBikeRequest): Bike {
        val bike = remote.createBike(request).toDomain()
        local.saveBike(bike.toEntity(currentTimeMillis = System.currentTimeMillis()))
        return bike
    }

    override suspend fun addBikeComponent(
        bikeId: String,
        request: AddComponentRequest
    ): Component {
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

    override suspend fun getStravaConnectionStatus(): StravaConnectionStatus {
        return remote.getStravaConnectionStatus().toDomain()
    }

    override suspend fun getStravaBikes(): List<StravaBike> {
        val bikes = mutableListOf<StravaBike>()
        for (dto in remote.getStravaBikes()) {
            bikes += dto.toDomain()
        }
        return bikes
    }
}
