package com.quetoquenana.and.features.bikes.data.remote.dataSource

interface BikeMediaUploadRemoteDataSource {
    suspend fun uploadFile(
        url: String,
        contentType: String,
        bytes: ByteArray
    )
}
