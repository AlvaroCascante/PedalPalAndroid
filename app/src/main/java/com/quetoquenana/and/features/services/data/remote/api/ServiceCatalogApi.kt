package com.quetoquenana.and.features.services.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.services.data.remote.dto.ProductPackageResponseDto
import com.quetoquenana.and.features.services.data.remote.dto.ProductResponseDto
import retrofit2.http.GET

interface ServiceCatalogApi {

    @GET("products/active")
    suspend fun getActiveProducts(): ApiResponse<List<ProductResponseDto>>

    @GET("packages/active")
    suspend fun getActivePackages(): ApiResponse<List<ProductPackageResponseDto>>
}
