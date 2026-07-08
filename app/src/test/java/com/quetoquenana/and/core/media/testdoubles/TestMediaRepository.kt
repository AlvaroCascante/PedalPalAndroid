package com.quetoquenana.and.core.media.testdoubles

import com.quetoquenana.and.core.media.data.repository.MediaRepositoryImpl
import com.quetoquenana.and.core.media.domain.repository.MediaRepository

/**
 * Test helper that wires MediaRepositoryImpl with the fake data sources.
 * Tests can access the underlying fakes to seed data or assert uploads.
 */
class TestMediaRepository(
    val localDataSource: FakeMediaLocalDataSource = FakeMediaLocalDataSource(),
    val remoteDataSource: FakeMediaRemoteDataSource = FakeMediaRemoteDataSource(),
    val uploadDataSource: FakeMediaUploadDataSource = FakeMediaUploadDataSource(),
) : MediaRepository by MediaRepositoryImpl(
    localDataSource,
    remoteDataSource,
    uploadDataSource,
) {
    // Expose convenience aliases if needed by tests
}
