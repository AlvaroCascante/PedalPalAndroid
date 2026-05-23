package com.quetoquenana.and.core.media.data.remote.dataSource

interface MediaUploadRemoteDataSource {
    suspend fun uploadFile(
        url: String,
        contentType: String,
        bytes: ByteArray,
    )
}

