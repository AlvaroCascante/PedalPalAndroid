package com.quetoquenana.and.di

import com.quetoquenana.and.features.authentication.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.features.authentication.data.remote.dataSource.FirebaseAuthDataSourceImpl
import com.quetoquenana.and.features.appointments.data.remote.dataSource.AppointmentsRemoteDataSource
import com.quetoquenana.and.features.appointments.data.remote.dataSource.AppointmentsRemoteDataSourceImpl
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository
import com.quetoquenana.and.features.appointments.data.repository.AppointmentsRepositoryImpl
import com.quetoquenana.and.features.suggestions.data.remote.dataSource.SuggestionsRemoteDataSource
import com.quetoquenana.and.features.suggestions.data.remote.dataSource.SuggestionsRemoteDataSourceImpl
import com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository
import com.quetoquenana.and.features.suggestions.data.repository.SuggestionsRepositoryImpl
import com.quetoquenana.and.features.announcements.data.remote.dataSource.AnnouncementRemoteDataSource
import com.quetoquenana.and.features.announcements.data.remote.dataSource.AnnouncementRemoteDataSourceImpl
import com.quetoquenana.and.features.announcements.domain.repository.AnnouncementRepository
import com.quetoquenana.and.features.announcements.data.repository.AnnouncementRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DomainModule {

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
    abstract fun bindsLandingRemoteDataSource(impl: AnnouncementRemoteDataSourceImpl): AnnouncementRemoteDataSource

    @Binds
    abstract fun bindsLandingRepository(impl: AnnouncementRepositoryImpl): AnnouncementRepository

}
