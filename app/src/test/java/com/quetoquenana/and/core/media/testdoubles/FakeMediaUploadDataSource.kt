package com.quetoquenana.and.core.media.testdoubles

import com.quetoquenana.and.core.media.data.remote.dataSource.MediaUploadDataSource

/**
 * Fake implementation of MediaUploadDataSource for unit tests.
 * - Records uploaded bytes by URL so tests can assert what was uploaded
 * - Configurable error mode to simulate network/storage failures
 */
class FakeMediaUploadDataSource : MediaUploadDataSource {

    private val uploads = mutableMapOf<String, ByteArray>()

    @Volatile
    private var errorMode: Boolean = false

    fun setErrorMode(enabled: Boolean) {
        errorMode = enabled
    }

    override suspend fun uploadFile(url: String, contentType: String, bytes: ByteArray) {
        if (errorMode) throw RuntimeException("FakeMediaUploadDataSource error")
        uploads[url] = bytes.copyOf()
    }

    fun getUploadedBytes(url: String): ByteArray? = uploads[url]

    fun clear() {
        uploads.clear()
    }
}
