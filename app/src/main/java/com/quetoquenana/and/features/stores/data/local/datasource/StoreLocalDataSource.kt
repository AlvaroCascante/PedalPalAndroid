package com.quetoquenana.and.features.stores.data.local.datasource

import com.quetoquenana.and.features.stores.data.local.entity.StoreEntity
import com.quetoquenana.and.features.stores.data.local.entity.StoreLocationEntity

interface StoreLocalDataSource {
    suspend fun getStores(): List<StoreEntity>
    suspend fun getLocationsForStore(storeId: String): List<StoreLocationEntity>
    suspend fun getLocationById(id: String): StoreLocationEntity?
    suspend fun saveStores(stores: List<StoreEntity>, locations: List<StoreLocationEntity>)
}
