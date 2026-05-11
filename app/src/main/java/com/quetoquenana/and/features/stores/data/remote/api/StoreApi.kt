package com.quetoquenana.and.features.stores.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.stores.data.remote.dto.StoreResponseDto
import retrofit2.http.GET

interface StoreApi {

    @GET("stores")
    suspend fun getStores(): ApiResponse<List<StoreResponseDto>>
}
