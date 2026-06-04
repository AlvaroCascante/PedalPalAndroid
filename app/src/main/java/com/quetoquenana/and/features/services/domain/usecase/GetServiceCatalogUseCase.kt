package com.quetoquenana.and.features.services.domain.usecase

import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import com.quetoquenana.and.features.services.domain.repository.ServiceCatalogRepository
import java.util.UUID
import javax.inject.Inject

class GetServiceCatalogUseCase @Inject constructor(
    private val repository: ServiceCatalogRepository
) {
    suspend operator fun invoke(storeLocationId: UUID, refresh: Boolean = false): ServiceCatalog {
        return repository.getCatalog(storeLocationId = storeLocationId, refresh = refresh)
    }
}
