package com.quetoquenana.and.features.authentication.data.remote.di

import com.quetoquenana.and.features.authentication.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.features.authentication.data.remote.dataSource.AuthRemoteDataSourceRetrofit
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRemoteModule {
    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        impl: AuthRemoteDataSourceRetrofit
    ): AuthRemoteDataSource
}