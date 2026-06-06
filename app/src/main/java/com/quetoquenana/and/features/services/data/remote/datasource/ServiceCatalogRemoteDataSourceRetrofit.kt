package com.quetoquenana.and.features.services.data.remote.datasource

import com.quetoquenana.and.core.network.networkCall
import com.quetoquenana.and.features.services.data.remote.api.ServiceCatalogApi
import com.quetoquenana.and.features.services.data.remote.dto.toDomain
import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import java.util.UUID
import javax.inject.Inject

class ServiceCatalogRemoteDataSourceRetrofit @Inject constructor(
    private val api: ServiceCatalogApi
) : ServiceCatalogRemoteDataSource {

    override suspend fun getCatalog(storeLocationId: UUID): ServiceCatalog {
        val packages = networkCall {
            api.getActivePackages(storeLocationId = storeLocationId)
        }.map { it.toDomain() }
        val products = networkCall {
            api.getActiveProducts(storeLocationId = storeLocationId)
        }.map { it.toDomain() }
        return ServiceCatalog(packages = packages, products = products)
    }
}
