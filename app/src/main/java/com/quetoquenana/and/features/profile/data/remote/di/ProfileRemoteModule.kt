package com.quetoquenana.and.features.profile.data.remote.di

import com.quetoquenana.and.features.profile.data.remote.dataSource.ProfileRemoteDataSource
import com.quetoquenana.and.features.profile.data.remote.dataSource.ProfileRemoteDataSourceRetrofit
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileRemoteModule {

    @Binds
    @Singleton
    abstract fun bindProfileRemoteDataSource(
        impl: ProfileRemoteDataSourceRetrofit,
    ): ProfileRemoteDataSource
}
