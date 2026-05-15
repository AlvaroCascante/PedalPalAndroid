package com.quetoquenana.and.features.stores.data.repository

import com.quetoquenana.and.features.stores.data.local.datasource.StoreLocalDataSource
import com.quetoquenana.and.features.stores.data.local.entity.toDomain
import com.quetoquenana.and.features.stores.data.local.entity.toEntity
import com.quetoquenana.and.features.stores.data.remote.dataSource.StoreRemoteDataSource
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.repository.StoreRepository
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val local: StoreLocalDataSource,
    private val remote: StoreRemoteDataSource
) : StoreRepository {

    override suspend fun getStores(refresh: Boolean): List<Store> {
        val cachedStores = local.getStores()
        if (refresh || cachedStores.isEmpty()) {
            val cachedFreshness = cachedStores
                .flatMap { store -> local.getLocationsForStore(storeId = store.id) }
                .associate { location -> location.id to location.serviceCatalogLastUpdatedAt }
            val stores = remote.getStores()
            val now = System.currentTimeMillis()
            local.saveStores(
                stores = stores.map { it.toEntity(currentTimeMillis = now) },
                locations = stores.flatMap { store ->
                    store.locations.map {
                        it.toEntity(
                            currentTimeMillis = now,
                            serviceCatalogLastUpdatedAt = cachedFreshness[it.id]
                        )
                    }
                }
            )
        }

        return local.getStores().map { storeEntity ->
            storeEntity.toDomain(
                locations = local.getLocationsForStore(storeId = storeEntity.id)
            )
        }
    }
}
