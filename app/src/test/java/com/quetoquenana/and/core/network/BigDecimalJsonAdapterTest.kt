package com.quetoquenana.and.core.network

import com.quetoquenana.and.features.appointments.data.remote.dto.AppointmentResponseDto
import com.quetoquenana.and.features.services.data.remote.dto.ProductPackageResponseDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BigDecimalJsonAdapterTest {

    private val moshi = Moshi.Builder()
        .add(BigDecimalJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun `service catalog response parses decimal prices from number and string values`() {
        val type = Types.newParameterizedType(
            ApiResponse::class.java,
            Types.newParameterizedType(List::class.java, ProductPackageResponseDto::class.java)
        )
        val adapter = moshi.adapter<ApiResponse<List<ProductPackageResponseDto>>>(type)

        val response = adapter.fromJson(
            """
            {
              "message": "ok",
              "data": [
                {
                  "id": "pkg-1",
                  "name": "Tune Up",
                  "description": "Full maintenance",
                  "price": 59.90,
                  "status": "ACTIVE",
                  "products": [
                    {
                      "id": "prod-1",
                      "name": "Chain clean",
                      "description": null,
                      "price": "12.50",
                      "status": "ACTIVE"
                    }
                  ]
                }
              ]
            }
            """.trimIndent()
        )

        assertNotNull(response)
        assertEquals(0, response!!.data.single().price?.compareTo(BigDecimal("59.90")))
        assertEquals(
            0,
            response.data.single().products.single().price?.compareTo(BigDecimal("12.50"))
        )
    }

    @Test
    fun `appointment response parses decimal fields used elsewhere in the app`() {
        val type = Types.newParameterizedType(
            ApiResponse::class.java,
            AppointmentResponseDto::class.java
        )
        val adapter = moshi.adapter<ApiResponse<AppointmentResponseDto>>(type)

        val response = adapter.fromJson(
            """
            {
              "message": "ok",
              "data": {
                "id": "appointment-1",
                "bikeId": "bike-1",
                "storeLocationId": "location-1",
                "scheduledAt": "2026-05-01T10:15:30Z",
                "status": "PENDING",
                "notes": "Bring spare tube",
                "deposit": "25.00",
                "requestedServices": [
                  {
                    "id": "service-1",
                    "productId": "prod-1",
                    "productNameSnapshot": "Chain clean",
                    "priceSnapshot": 12.50
                  }
                ]
              }
            }
            """.trimIndent()
        )

        assertNotNull(response)
        assertEquals(0, response!!.data.deposit?.compareTo(BigDecimal("25.00")))
        assertEquals(
            0,
            response.data.requestedServices.single().priceSnapshot?.compareTo(BigDecimal("12.50"))
        )
    }
}



