package com.quetoquenana.and.features.appointments.data.remote.api

import com.quetoquenana.and.core.network.ApiResponse
import com.quetoquenana.and.features.appointments.data.remote.dto.AppointmentListItemResponseDto
import com.quetoquenana.and.features.appointments.data.remote.dto.AppointmentResponseDto
import com.quetoquenana.and.features.appointments.data.remote.dto.ChangeAppointmentStatusRequestDto
import com.quetoquenana.and.features.appointments.data.remote.dto.ChangeAppointmentStatusResponseDto
import com.quetoquenana.and.features.appointments.data.remote.dto.CreateAppointmentRequestDto
import com.quetoquenana.and.features.appointments.data.remote.dto.UpdateAppointmentRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AppointmentApi {

    @GET("appointments")
    suspend fun getAppointments(): ApiResponse<List<AppointmentListItemResponseDto>>

    @GET("appointments/{id}")
    suspend fun getAppointment(@Path("id") id: String): ApiResponse<AppointmentResponseDto>

    @POST("appointments")
    suspend fun createAppointment(
        @Body request: CreateAppointmentRequestDto
    ): ApiResponse<AppointmentResponseDto>

    @PATCH("appointments/{id}/reschedule")
    suspend fun rescheduleAppointment(
        @Path("id") id: String,
        @Body request: UpdateAppointmentRequestDto
    ): ApiResponse<AppointmentResponseDto>

    @PATCH("appointments/{id}/status")
    suspend fun changeAppointmentStatus(
        @Path("id") id: String,
        @Body request: ChangeAppointmentStatusRequestDto
    ): ApiResponse<ChangeAppointmentStatusResponseDto>
}
