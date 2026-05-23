package com.quetoquenana.and.di

import com.quetoquenana.and.features.authentication.data.remote.dataSource.FirebaseAuthDataSource
import com.quetoquenana.and.features.authentication.data.remote.dataSource.FirebaseAuthDataSourceImpl
import com.quetoquenana.and.features.appointments.data.remote.dataSource.AppointmentRemoteDataSource
import com.quetoquenana.and.features.appointments.data.remote.dataSource.AppointmentRemoteDataSourceRetrofitImpl
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentRepository
import com.quetoquenana.and.features.appointments.data.repository.AppointmentRepositoryImpl
import com.quetoquenana.and.features.suggestions.data.remote.dataSource.SuggestionsRemoteDataSource
import com.quetoquenana.and.features.suggestions.data.remote.dataSource.SuggestionsRemoteDataSourceImpl
import com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository
import com.quetoquenana.and.features.suggestions.data.repository.SuggestionsRepositoryImpl
import com.quetoquenana.and.features.announcements.data.remote.dataSource.AnnouncementRemoteDataSource
import com.quetoquenana.and.features.announcements.data.remote.dataSource.AnnouncementRemoteDataSourceRetrofit
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
    abstract fun bindsAppointmentRemoteDataSource(impl: AppointmentRemoteDataSourceRetrofitImpl): AppointmentRemoteDataSource

    @Binds
    abstract fun bindsAppointmentRepository(impl: AppointmentRepositoryImpl): AppointmentRepository

    // Suggestions bindings
    @Binds
    abstract fun bindsSuggestionsRemoteDataSource(impl: SuggestionsRemoteDataSourceImpl): SuggestionsRemoteDataSource

    @Binds
    abstract fun bindsSuggestionsRepository(impl: SuggestionsRepositoryImpl): SuggestionsRepository

    // Announcement bindings
    @Binds
    abstract fun bindsAnnouncementRemoteDataSource(impl: AnnouncementRemoteDataSourceRetrofit): AnnouncementRemoteDataSource

    @Binds
    abstract fun bindsAnnouncementRepository(impl: AnnouncementRepositoryImpl): AnnouncementRepository

}
