package com.quetoquenana.and.core.media.data.remote.api

import com.quetoquenana.and.core.media.domain.model.CreateMediaRequestDto
import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.core.utils.APP_NAME_PEDALPAL
import com.quetoquenana.and.core.utils.HEADER_APP_NAME
import com.quetoquenana.and.core.utils.PARAMETER_ID
import com.quetoquenana.and.core.utils.PARAMETER_REFERENCE_TYPE
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface MediaApi {

    @GET(value = "media/{referenceType}/{id}")
    suspend fun getMedia(
        @Path(value = PARAMETER_REFERENCE_TYPE) referenceType: String,
        @Path(value = PARAMETER_ID) id: UUID,
    ): ApiResponse<List<MediaFileResponseDto>>

    @POST(value = "media/{id}")
    suspend fun createMedia(
        @Path(value = PARAMETER_ID) id: UUID,
        @Body request: CreateMediaRequestDto,
        @Header(value = HEADER_APP_NAME) applicationName: String = APP_NAME_PEDALPAL,
    ): ApiResponse<List<MediaFileResponseDto>>

    @POST(value = "media/{id}/confirm")
    suspend fun confirmMedia(@Path(value = PARAMETER_ID) id: UUID): ApiResponse<MediaFileResponseDto>
}

