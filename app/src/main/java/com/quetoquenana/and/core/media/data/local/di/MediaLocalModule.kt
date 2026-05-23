package com.quetoquenana.and.core.media.data.local.di

import com.quetoquenana.and.core.database.AppDatabase
import com.quetoquenana.and.core.media.data.local.dao.MediaDao
import com.quetoquenana.and.core.media.data.local.datasource.MediaLocalDataSource
import com.quetoquenana.and.core.media.data.local.datasource.MediaLocalDataSourceRoom
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaLocalModule {

    @Binds
    @Singleton
    abstract fun bindMediaLocalDataSource(
        impl: MediaLocalDataSourceRoom,
    ): MediaLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object MediaLocalDaoModule {

    @Provides
    fun provideMediaDao(database: AppDatabase): MediaDao {
        return database.mediaDao()
    }
}


