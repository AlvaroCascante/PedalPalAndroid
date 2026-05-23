package com.quetoquenana.and.features.services.data.remote.datasource

import com.quetoquenana.and.features.services.data.remote.api.ServiceCatalogApi
import com.quetoquenana.and.features.services.data.remote.dto.toDomain
import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import javax.inject.Inject

class ServiceCatalogRemoteDataSourceRetrofit @Inject constructor(
    private val api: ServiceCatalogApi
) : ServiceCatalogRemoteDataSource {

    override suspend fun getCatalog(storeLocationId: String): ServiceCatalog {
        val packages = api.getActivePackages(storeLocationId = storeLocationId).data.map { it.toDomain() }
        val products = api.getActiveProducts(storeLocationId = storeLocationId).data.map { it.toDomain() }
        return ServiceCatalog(packages = packages, products = products)
    }
}
