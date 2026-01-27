package com.quetoquenana.and.pedalpal.di

import com.quetoquenana.and.pedalpal.feature.auth.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.pedalpal.feature.auth.data.remote.dataSource.AuthRemoteDataSourceImpl
import com.quetoquenana.and.pedalpal.feature.auth.domain.repository.AuthRepository
import com.quetoquenana.and.pedalpal.feature.auth.data.repository.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DomainModule {

    @Binds
    abstract fun bindsAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindsAuthRemoteDataSource(authRemoteDataSource: AuthRemoteDataSourceImpl): AuthRemoteDataSource
}