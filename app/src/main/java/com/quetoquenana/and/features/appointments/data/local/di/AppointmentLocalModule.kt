package com.quetoquenana.and.features.appointments.data.local.di

import com.quetoquenana.and.core.database.AppDatabase
import com.quetoquenana.and.features.appointments.data.local.dao.AppointmentDao
import com.quetoquenana.and.features.appointments.data.local.datasource.AppointmentLocalDataSource
import com.quetoquenana.and.features.appointments.data.local.datasource.AppointmentLocalDataSourceRoom
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppointmentLocalDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindAppointmentLocalDataSource(
        impl: AppointmentLocalDataSourceRoom
    ): AppointmentLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object AppointmentLocalDaoModule {
    @Provides
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao {
        return database.appointmentDao()
    }
}
