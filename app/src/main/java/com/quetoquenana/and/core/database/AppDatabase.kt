package com.quetoquenana.and.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quetoquenana.and.features.authentication.data.local.dao.AuthSessionDao
import com.quetoquenana.and.features.authentication.data.local.dao.AuthUserDao
import com.quetoquenana.and.features.authentication.data.local.entity.AuthSessionEntity
import com.quetoquenana.and.features.authentication.data.local.entity.AuthUserEntity
import com.quetoquenana.and.features.appointments.data.local.dao.AppointmentDao
import com.quetoquenana.and.features.appointments.data.local.entity.AppointmentEntity
import com.quetoquenana.and.features.appointments.data.local.entity.AppointmentServiceEntity
import com.quetoquenana.and.features.bikes.data.local.dao.BikeComponentDao
import com.quetoquenana.and.features.bikes.data.local.dao.BikeDao
import com.quetoquenana.and.features.bikes.data.local.entity.BikeComponentEntity
import com.quetoquenana.and.features.bikes.data.local.entity.BikeEntity
import com.quetoquenana.and.features.profile.data.local.dao.ProfileDao
import com.quetoquenana.and.features.profile.data.local.entity.ProfileEntity
import com.quetoquenana.and.features.services.data.local.dao.ServiceCatalogDao
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageEntity
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageProductEntity
import com.quetoquenana.and.features.services.data.local.entity.ServiceProductEntity
import com.quetoquenana.and.features.stores.data.local.dao.StoreDao
import com.quetoquenana.and.features.stores.data.local.entity.StoreEntity
import com.quetoquenana.and.features.stores.data.local.entity.StoreLocationEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Database(
    entities = [
        AuthUserEntity::class,
        AuthSessionEntity::class,
        BikeEntity::class,
        BikeComponentEntity::class,
        ProfileEntity::class,
        StoreEntity::class,
        StoreLocationEntity::class,
        ServiceProductEntity::class,
        ServicePackageEntity::class,
        ServicePackageProductEntity::class,
        AppointmentEntity::class,
        AppointmentServiceEntity::class
    ],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authUserDao(): AuthUserDao
    abstract fun authSessionDao(): AuthSessionDao
    abstract fun bikeDao(): BikeDao
    abstract fun bikeComponentDao(): BikeComponentDao
    abstract fun profileDao(): ProfileDao
    abstract fun storeDao(): StoreDao
    abstract fun serviceCatalogDao(): ServiceCatalogDao
    abstract fun appointmentDao(): AppointmentDao
}

// DI Module to provide the AppDatabase instance
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pedalpal.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }
}
