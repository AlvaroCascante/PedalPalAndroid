package com.quetoquenana.and.bikes.data.repository

import com.quetoquenana.and.features.bikes.data.local.datasource.BikeComponentLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.entity.BikeComponentEntity
import com.quetoquenana.and.features.bikes.data.local.entity.BikeEntity
import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeRemoteDataSource
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeComponentDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.BikeHistoryDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaBikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectUrlDto
import com.quetoquenana.and.features.bikes.data.remote.dto.SystemCodeDto
import com.quetoquenana.and.features.bikes.data.repository.BikeRepositoryImpl
import com.quetoquenana.and.features.bikes.domain.model.AddBikeComponentRequest
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
            )
        )

        val result = repository.getBikeComponentTypes()

        assertEquals(listOf("CHAIN", "CASSETTE", "OTHER"), result.map { it.code })
        assertEquals(listOf("Chain", "Cassette", "Other"), result.map { it.codeDescription })
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
        private val componentTypes: Set<SystemCodeDto>
    ) : BikeRemoteDataSource {
        override suspend fun getBikeComponentTypes(): Set<SystemCodeDto> = componentTypes

        override suspend fun getBikes(): List<BikeDto> = emptyList()

        override suspend fun getBike(id: String): BikeDto = error("Not needed")

        override suspend fun getBikeHistory(id: String): List<BikeHistoryDto> = emptyList()

        override suspend fun createBike(request: CreateBikeRequest): BikeDto = error("Not needed")

        override suspend fun addBikeComponent(
            bikeId: String,
            request: AddBikeComponentRequest
        ): BikeComponentDto = error("Not needed")

        override suspend fun getStravaConnectUrl(): StravaConnectUrlDto = error("Not needed")

        override suspend fun getStravaBikes(): List<StravaBikeDto> = emptyList()
    }

    private companion object {
        fun systemCode(
            code: String,
            description: String,
            position: Int?
        ): SystemCodeDto {
            return SystemCodeDto(
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
