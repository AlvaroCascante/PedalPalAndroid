package com.quetoquenana.and.core.media.testdoubles

import com.quetoquenana.and.core.media.data.local.datasource.MediaLocalDataSource
import com.quetoquenana.and.core.media.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

/**
 * Lightweight in-memory fake of MediaLocalDataSource for unit tests.
 * - Deterministic MutableStateFlow per (referenceId, referenceType)
 * - Thread-safe via Mutex
 * - Configurable error mode for negative-path testing
 */
class FakeMediaLocalDataSource : MediaLocalDataSource {

    private val mutex = Mutex()

    // Storage keyed by (referenceId, referenceType)
    private val storage = mutableMapOf<Pair<UUID, String>, MutableList<MediaEntity>>()

    // Flows for observers
    private val flows = mutableMapOf<Pair<UUID, String>, MutableStateFlow<List<MediaEntity>>>()

    @Volatile
    private var errorMode: Boolean = false

    fun setErrorMode(enabled: Boolean) {
        errorMode = enabled
    }

    suspend fun seedMedia(referenceId: UUID, referenceType: String, media: List<MediaEntity>) {
        val key = referenceId to referenceType
        mutex.withLock {
            storage[key] = media.toMutableList()
            flows.getOrPut(key) { MutableStateFlow(media) }.value = media.toList()
        }
    }

    private fun getOrCreateFlow(key: Pair<UUID, String>): MutableStateFlow<List<MediaEntity>> {
        return flows.getOrPut(key) {
            val list = storage[key]?.toList() ?: emptyList()
            MutableStateFlow(list)
        }
    }

    override fun observeMedia(referenceId: UUID, referenceType: String): Flow<List<MediaEntity>> {
        val key = referenceId to referenceType
        return getOrCreateFlow(key).asStateFlow()
    }

    override suspend fun getMedia(referenceId: UUID, referenceType: String): List<MediaEntity> {
        if (errorMode) throw RuntimeException("FakeMediaLocalDataSource error")
        val key = referenceId to referenceType
        return mutex.withLock { storage[key]?.toList() ?: emptyList() }
    }

    override suspend fun getSingleMedia(referenceId: UUID, referenceType: String): MediaEntity? {
        if (errorMode) throw RuntimeException("FakeMediaLocalDataSource error")
        val key = referenceId to referenceType
        return mutex.withLock { storage[key]?.firstOrNull() }
    }

    override suspend fun saveAllMedia(media: List<MediaEntity>) {
        if (errorMode) throw RuntimeException("FakeMediaLocalDataSource error")
        mutex.withLock {
            // Group incoming entities by their reference id/type and replace that group's storage
            media.groupBy { it.referenceId to it.referenceType }
                .forEach { (key, list) ->
                    storage[key] = list.toMutableList()
                    flows.getOrPut(key) { MutableStateFlow(list) }.value = list.toList()
                }
        }
    }

    override suspend fun updateMedia(media: MediaEntity) {
        if (errorMode) throw RuntimeException("FakeMediaLocalDataSource error")
        val key = media.referenceId to media.referenceType
        mutex.withLock {
            val list = storage.getOrPut(key) { mutableListOf() }
            val index = list.indexOfFirst { it.mediaId == media.mediaId }
            if (index >= 0) {
                list[index] = media
            } else {
                list.add(media)
            }
            flows.getOrPut(key) { MutableStateFlow(list.toList()) }.value = list.toList()
        }
    }

    override suspend fun clearMedia() {
        if (errorMode) throw RuntimeException("FakeMediaLocalDataSource error")
        mutex.withLock {
            storage.clear()
            flows.values.forEach { it.value = emptyList() }
        }
    }
}