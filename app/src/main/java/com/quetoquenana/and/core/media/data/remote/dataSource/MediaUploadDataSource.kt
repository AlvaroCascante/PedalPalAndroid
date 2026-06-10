package com.quetoquenana.and.core.media.data.remote.dataSource

interface MediaUploadDataSource {
    suspend fun uploadFile(
        url: String,
        contentType: String,
        bytes: ByteArray,
    )
}

