package com.quetoquenana.and.features.services.data.remote.datasource

import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import java.util.UUID

interface ServiceCatalogRemoteDataSource {
    suspend fun getCatalog(storeLocationId: UUID): ServiceCatalog
}
