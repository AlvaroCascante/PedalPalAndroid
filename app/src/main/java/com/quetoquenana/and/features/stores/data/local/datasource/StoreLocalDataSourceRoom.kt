package com.quetoquenana.and.features.stores.data.local.datasource

import com.quetoquenana.and.features.stores.data.local.dao.StoreDao
import com.quetoquenana.and.features.stores.data.local.entity.StoreEntity
import com.quetoquenana.and.features.stores.data.local.entity.StoreLocationEntity
import java.util.UUID
import javax.inject.Inject

class StoreLocalDataSourceRoom @Inject constructor(
    private val storeDao: StoreDao
) : StoreLocalDataSource {

    override suspend fun getStores(): List<StoreEntity> = storeDao.getStores()

    override suspend fun getStoreLocationsByStoreId(storeId: UUID): List<StoreLocationEntity> {
        return storeDao.getStoreLocationsByStoreId(storeId = storeId)
    }

    override suspend fun getStoreLocationById(id: UUID): StoreLocationEntity? {
        return storeDao.getStoreLocationById(id = id)
    }

    override suspend fun saveStores(
        stores: List<StoreEntity>,
        locations: List<StoreLocationEntity>
    ) {
        storeDao.replaceAll(stores = stores, locations = locations)
    }
}
