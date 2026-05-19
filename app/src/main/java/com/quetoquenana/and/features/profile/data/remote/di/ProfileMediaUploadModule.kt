package com.quetoquenana.and.features.profile.data.remote.di

import com.quetoquenana.and.features.profile.data.remote.dataSource.ProfileMediaUploadRemoteDataSource
import com.quetoquenana.and.features.profile.data.remote.dataSource.ProfileMediaUploadRemoteDataSourceOkHttp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileMediaUploadModule {

    @Binds
    @Singleton
    abstract fun bindProfileMediaUploadRemoteDataSource(
        impl: ProfileMediaUploadRemoteDataSourceOkHttp,
    ): ProfileMediaUploadRemoteDataSource

    companion object {
        @Provides
        @Singleton
        @Named("profileMediaUploadClient")
        fun provideProfileMediaUploadClient(
            loggingInterceptor: HttpLoggingInterceptor,
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }
    }
}