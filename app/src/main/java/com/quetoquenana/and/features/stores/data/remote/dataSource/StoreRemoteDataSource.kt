package com.quetoquenana.and.features.stores.data.remote.dataSource

import com.quetoquenana.and.features.stores.domain.model.Store

interface StoreRemoteDataSource {
    suspend fun getStores(): List<Store>
}
