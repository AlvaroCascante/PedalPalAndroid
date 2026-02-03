package com.quetoquenana.and.pedalpal.di

import com.quetoquenana.and.pedalpal.feature.login.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.pedalpal.feature.login.data.remote.dataSource.AuthRemoteDataSourceImpl
import com.quetoquenana.and.pedalpal.feature.login.domain.repository.AuthRepository
import com.quetoquenana.and.pedalpal.feature.login.data.repository.AuthRepositoryImpl
import com.quetoquenana.and.pedalpal.feature.login.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.pedalpal.feature.login.data.remote.dataSource.FirebaseAuthDataSourceImpl
import com.quetoquenana.and.pedalpal.feature.appointments.data.remote.dataSource.AppointmentsRemoteDataSource
import com.quetoquenana.and.pedalpal.feature.appointments.data.remote.dataSource.AppointmentsRemoteDataSourceImpl
import com.quetoquenana.and.pedalpal.feature.appointments.domain.repository.AppointmentsRepository
import com.quetoquenana.and.pedalpal.feature.appointments.data.repository.AppointmentsRepositoryImpl
import com.quetoquenana.and.pedalpal.feature.suggestions.data.remote.dataSource.SuggestionsRemoteDataSource
import com.quetoquenana.and.pedalpal.feature.suggestions.data.remote.dataSource.SuggestionsRemoteDataSourceImpl
import com.quetoquenana.and.pedalpal.feature.suggestions.domain.repository.SuggestionsRepository
import com.quetoquenana.and.pedalpal.feature.suggestions.data.repository.SuggestionsRepositoryImpl
import com.quetoquenana.and.pedalpal.feature.landing.data.remote.dataSource.LandingRemoteDataSource
import com.quetoquenana.and.pedalpal.feature.landing.data.remote.dataSource.LandingRemoteDataSourceImpl
import com.quetoquenana.and.pedalpal.feature.landing.domain.repository.LandingRepository
import com.quetoquenana.and.pedalpal.feature.landing.data.repository.LandingRepositoryImpl
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

    // Appointments bindings
    @Binds
    abstract fun bindsAppointmentsRemoteDataSource(impl: AppointmentsRemoteDataSourceImpl): AppointmentsRemoteDataSource

    @Binds
    abstract fun bindsAppointmentsRepository(impl: AppointmentsRepositoryImpl): AppointmentsRepository

    // Suggestions bindings
    @Binds
    abstract fun bindsSuggestionsRemoteDataSource(impl: SuggestionsRemoteDataSourceImpl): SuggestionsRemoteDataSource

    @Binds
    abstract fun bindsSuggestionsRepository(impl: SuggestionsRepositoryImpl): SuggestionsRepository

    // Landing bindings
    @Binds
    abstract fun bindsLandingRemoteDataSource(impl: LandingRemoteDataSourceImpl): LandingRemoteDataSource

    @Binds
    abstract fun bindsLandingRepository(impl: LandingRepositoryImpl): LandingRepository

}