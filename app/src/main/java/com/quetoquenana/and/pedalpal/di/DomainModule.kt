package com.quetoquenana.and.pedalpal.di

import com.quetoquenana.and.pedalpal.feature.login.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.pedalpal.feature.login.data.remote.dataSource.AuthRemoteDataSourceImpl
import com.quetoquenana.and.pedalpal.feature.login.domain.repository.AuthRepository
import com.quetoquenana.and.pedalpal.feature.login.data.repository.AuthRepositoryImpl
import com.quetoquenana.and.pedalpal.feature.login.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.pedalpal.feature.login.data.remote.dataSource.FirebaseAuthDataSourceImpl
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

    @Binds
    abstract fun bindsFirebaseAuthDataSource(firebaseAuthDataSource: FirebaseAuthDataSourceImpl): FirebaseAuthDataSource

}