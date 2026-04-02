package com.quetoquenana.and.features.authentication.di

import com.quetoquenana.and.features.authentication.data.repository.AuthRepositoryImpl
import com.quetoquenana.and.features.authentication.data.repository.FirebaseRepositoryImpl
import com.quetoquenana.and.features.authentication.domain.repository.AuthRepository
import com.quetoquenana.and.features.authentication.domain.repository.FirebaseRepository
import com.quetoquenana.and.features.authentication.session.TokenProvider
import com.quetoquenana.and.features.authentication.session.TokenProviderImpl
import com.quetoquenana.and.features.authentication.session.TokenStorage
import com.quetoquenana.and.features.authentication.session.TokenStorageImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindsAuthRepository(
        repository: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenProvider(
        impl: TokenProviderImpl
    ): TokenProvider

    @Binds
    @Singleton
    abstract fun bindTokenStorage(
        impl: TokenStorageImpl
    ): TokenStorage


    @Binds
    @Singleton
    abstract fun bindAuthQueryService(
        impl: FirebaseRepositoryImpl
    ): FirebaseRepository
}