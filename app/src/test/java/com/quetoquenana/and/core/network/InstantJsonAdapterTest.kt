package com.quetoquenana.and.core.network

import com.quetoquenana.and.core.media.domain.model.MediaFileResponseDto
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class InstantJsonAdapterTest {

    private val moshi = Moshi.Builder()
        .add(InstantJsonAdapter())
        .add(UuidJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun `media response parses ISO-8601 expiresAt into Instant`() {
        val adapter = moshi.adapter(MediaFileResponseDto::class.java)

        val response = adapter.fromJson(
            """
            {
              "id": "11111111-1111-1111-1111-111111111111",
              "correlationId": "22222222-2222-2222-2222-222222222222",
              "contentType": "image/png",
              "provider": "S3",
              "status": "COMPLETED",
              "name": "profile.png",
              "altText": "Profile image",
              "url": "https://example.com/profile.png",
              "expiresAt": "2026-05-28T22:18:59.383200812Z",
              "isPublic": true
            }
            """.trimIndent()
        )

        assertEquals(Instant.parse("2026-05-28T22:18:59.383200812Z"), response?.expiresAt)
    }

    @Test
    fun `media response supports null expiresAt`() {
        val adapter = moshi.adapter(MediaFileResponseDto::class.java)

        val response = adapter.fromJson(
            """
            {
              "id": "11111111-1111-1111-1111-111111111111",
              "correlationId": null,
              "contentType": "image/png",
              "provider": "S3",
              "status": "COMPLETED",
              "name": "profile.png",
              "altText": null,
              "url": "https://example.com/profile.png",
              "expiresAt": null,
              "isPublic": true
            }
            """.trimIndent()
        )

        assertNull(response?.expiresAt)
    }

    @Test(expected = JsonDataException::class)
    fun `invalid instant string throws JsonDataException`() {
        val adapter = moshi.adapter(MediaFileResponseDto::class.java)

        adapter.fromJson(
            """
            {
              "id": "11111111-1111-1111-1111-111111111111",
              "correlationId": null,
              "contentType": "image/png",
              "provider": "S3",
              "status": "COMPLETED",
              "name": "profile.png",
              "altText": null,
              "url": "https://example.com/profile.png",
              "expiresAt": "not-an-instant",
              "isPublic": true
            }
            """.trimIndent()
        )
    }
}

