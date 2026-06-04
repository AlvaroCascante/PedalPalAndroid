package com.quetoquenana.and.features.services.domain.repository

import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import java.util.UUID

interface ServiceCatalogRepository {
    suspend fun getCatalog(storeLocationId: UUID, refresh: Boolean = false): ServiceCatalog
}
