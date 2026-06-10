package com.quetoquenana.and.core.media.data.remote.dataSource

import com.quetoquenana.and.core.utils.HEADER_CONTENT_TYPE
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MediaUploadDataSourceOkHttp @Inject constructor(
    @param:Named("mediaUploadClient") private val okHttpClient: OkHttpClient,
) : MediaUploadDataSource {

    override suspend fun uploadFile(
        url: String,
        contentType: String,
        bytes: ByteArray,
    ) = withContext(context = Dispatchers.IO) {
        val request = Request.Builder()
            .url(url = url)
            .header(name = HEADER_CONTENT_TYPE, value = contentType)
            .put(body = bytes.toRequestBody(contentType = contentType.toMediaType()))
            .build()

        okHttpClient.newCall(request = request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Unable to upload selected media")
            }
        }
    }
}

