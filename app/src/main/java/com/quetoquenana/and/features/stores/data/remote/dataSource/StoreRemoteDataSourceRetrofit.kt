package com.quetoquenana.and.features.stores.data.remote.dataSource

import com.quetoquenana.and.core.network.networkCall
import com.quetoquenana.and.features.stores.data.remote.api.StoreApi
import com.quetoquenana.and.features.stores.data.remote.dto.toDomain
import com.quetoquenana.and.features.stores.domain.model.Store
import javax.inject.Inject

class StoreRemoteDataSourceRetrofit @Inject constructor(
    private val api: StoreApi
) : StoreRemoteDataSource {

    override suspend fun getStores(): List<Store> {
        return networkCall {
            api.getStores()
        }.map { it.toDomain() }
    }
}
