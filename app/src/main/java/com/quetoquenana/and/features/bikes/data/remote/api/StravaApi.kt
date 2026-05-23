package com.quetoquenana.and.features.bikes.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectionStatusDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaBikeDto
import com.quetoquenana.and.features.bikes.data.remote.dto.StravaConnectUrlDto
import retrofit2.http.GET
import retrofit2.http.Header

interface StravaApi {

    @GET("strava/connect-url")
    suspend fun getStravaConnectUrl(
        @Header("X-Application-Name") applicationName: String = "PEDPAL"
    ): ApiResponse<StravaConnectUrlDto>

    @GET("strava/connection/status")
    suspend fun getStravaConnectionStatus(): ApiResponse<StravaConnectionStatusDto>

    @GET("strava/bikes")
    suspend fun getStravaBikes(): ApiResponse<List<StravaBikeDto>>
}
