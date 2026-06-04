package com.quetoquenana.and.features.stores.data.local.datasource

import com.quetoquenana.and.features.stores.data.local.entity.StoreEntity
import com.quetoquenana.and.features.stores.data.local.entity.StoreLocationEntity
import java.util.UUID

interface StoreLocalDataSource {
    suspend fun getStores(): List<StoreEntity>
    suspend fun getStoreLocationsByStoreId(storeId: UUID): List<StoreLocationEntity>
    suspend fun getStoreLocationById(id: UUID): StoreLocationEntity?
    suspend fun saveStores(stores: List<StoreEntity>, locations: List<StoreLocationEntity>)
}
