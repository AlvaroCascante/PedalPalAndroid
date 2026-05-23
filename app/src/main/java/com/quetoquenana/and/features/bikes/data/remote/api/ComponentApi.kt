package com.quetoquenana.and.features.bikes.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.bikes.data.remote.dto.ComponentDto
import retrofit2.http.GET

interface ComponentApi {

    @GET("components")
    suspend fun getComponentTypes(): ApiResponse<Set<ComponentDto>>
}
