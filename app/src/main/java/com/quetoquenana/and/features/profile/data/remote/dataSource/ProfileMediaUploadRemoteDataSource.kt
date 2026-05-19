package com.quetoquenana.and.features.profile.data.remote.dataSource

interface ProfileMediaUploadRemoteDataSource {
    suspend fun uploadFile(
        url: String,
        contentType: String,
        bytes: ByteArray,
    )
}

