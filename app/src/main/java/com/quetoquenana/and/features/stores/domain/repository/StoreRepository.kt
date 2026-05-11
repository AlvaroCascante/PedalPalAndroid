package com.quetoquenana.and.features.stores.domain.repository

import com.quetoquenana.and.features.stores.domain.model.Store

interface StoreRepository {
    suspend fun getStores(refresh: Boolean = false): List<Store>
}
