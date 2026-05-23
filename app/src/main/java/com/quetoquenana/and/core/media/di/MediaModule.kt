package com.quetoquenana.and.core.media.di

import com.quetoquenana.and.core.media.data.repository.MediaRepositoryImpl
import com.quetoquenana.and.core.media.domain.repository.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaModule {

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        impl: MediaRepositoryImpl,
    ): MediaRepository
}

