package com.quetoquenana.and.features.services.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.services.data.remote.dto.ProductPackageResponseDto
import com.quetoquenana.and.features.services.data.remote.dto.ProductResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceCatalogApi {

    @GET("products/active")
    suspend fun getActiveProducts(
        @Query("store_location_id") storeLocationId: String
    ): ApiResponse<List<ProductResponseDto>>

    @GET("packages/active")
    suspend fun getActivePackages(
        @Query("store_location_id") storeLocationId: String
    ): ApiResponse<List<ProductPackageResponseDto>>
}
