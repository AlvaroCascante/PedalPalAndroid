package com.quetoquenana.and.core.network

import com.quetoquenana.and.features.appointments.data.remote.dto.CreateAppointmentRequestDto
import com.quetoquenana.and.features.appointments.data.remote.dto.RequestedServiceItemRequestDto
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class UuidJsonAdapterTest {

    private val moshi = Moshi.Builder()
        .add(UuidJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun `request dto serializes UUID fields as canonical strings`() {
        val adapter = moshi.adapter(CreateAppointmentRequestDto::class.java)
        val request = CreateAppointmentRequestDto(
            bikeId = UUID.fromString("11111111-1111-1111-1111-111111111111"),
            storeLocationId = UUID.fromString("22222222-2222-2222-2222-222222222222"),
            scheduledAt = "2026-05-01T10:15:30Z",
            customerId = UUID.fromString("33333333-3333-3333-3333-333333333333"),
            notes = "Bring spare tube",
            requestedServices = listOf(
                RequestedServiceItemRequestDto(
                    serviceId = UUID.fromString("44444444-4444-4444-4444-444444444444"),
                    serviceType = "SERVICE"
                )
            )
        )

        val json = adapter.toJson(request)

        assertEquals(
            """
            {"bikeId":"11111111-1111-1111-1111-111111111111","storeLocationId":"22222222-2222-2222-2222-222222222222","scheduledAt":"2026-05-01T10:15:30Z","customerId":"33333333-3333-3333-3333-333333333333","notes":"Bring spare tube","requestedServices":[{"serviceId":"44444444-4444-4444-4444-444444444444","serviceType":"SERVICE"}]}
            """.trimIndent(),
            json
        )
    }

    @Test(expected = JsonDataException::class)
    fun `invalid UUID string throws JsonDataException`() {
        val adapter = moshi.adapter(UUID::class.java)

        adapter.fromJson("\"not-a-uuid\"")
    }
}

