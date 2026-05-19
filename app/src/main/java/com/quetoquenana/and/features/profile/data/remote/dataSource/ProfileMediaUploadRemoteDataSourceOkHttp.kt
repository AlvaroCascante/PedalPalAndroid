package com.quetoquenana.and.features.profile.data.remote.dataSource

import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileMediaUploadRemoteDataSourceOkHttp @Inject constructor(
    @param:Named("profileMediaUploadClient") private val okHttpClient: OkHttpClient,
) : ProfileMediaUploadRemoteDataSource {

    override suspend fun uploadFile(
        url: String,
        contentType: String,
        bytes: ByteArray,
    ) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .header("Content-Type", contentType)
            .put(bytes.toRequestBody(contentType.toMediaType()))
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Unable to upload selected profile image")
            }
        }
    }
}

