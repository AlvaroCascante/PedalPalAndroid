package com.quetoquenana.and.features.stores.data.local.datasource

import com.quetoquenana.and.features.stores.data.local.dao.StoreDao
import com.quetoquenana.and.features.stores.data.local.entity.StoreEntity
import com.quetoquenana.and.features.stores.data.local.entity.StoreLocationEntity
import javax.inject.Inject

class StoreLocalDataSourceRoom @Inject constructor(
    private val storeDao: StoreDao
) : StoreLocalDataSource {

    override suspend fun getStores(): List<StoreEntity> = storeDao.getStores()

    override suspend fun getLocationsForStore(storeId: String): List<StoreLocationEntity> {
        return storeDao.getLocationsForStore(storeId = storeId)
    }

    override suspend fun saveStores(
        stores: List<StoreEntity>,
        locations: List<StoreLocationEntity>
    ) {
        storeDao.replaceAll(stores = stores, locations = locations)
    }
}
