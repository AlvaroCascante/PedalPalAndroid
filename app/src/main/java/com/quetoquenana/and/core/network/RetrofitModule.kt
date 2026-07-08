package com.quetoquenana.and.core.network


import com.quetoquenana.and.core.utils.MODULE_MAIN_HTTP_CLIENT
import com.quetoquenana.and.core.utils.MODULE_PEDALPAL_SERVICE_RETROFIT
import com.quetoquenana.and.core.utils.MODULE_REFRESH_HTTP_CLIENT
import com.quetoquenana.and.core.utils.MODULE_REFRESH_RETROFIT
import com.quetoquenana.and.core.utils.MODULE_USER_SERVICE_RETROFIT
import com.quetoquenana.and.features.authentication.data.remote.api.AuthRefreshApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkConstants {
    const val USER_SERVICE_BASE_URL     = "https://user-service.quetoquenana.com/userservice/api/"
    const val PEDALPAL_SERVICE_BASE_URL = "https://pedalpal-service.quetoquenana.com/pedalpalservice/v1/api/"
}

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(BigDecimalJsonAdapter())
            .add(InstantJsonAdapter())
            .add(UuidJsonAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named(value = MODULE_MAIN_HTTP_CLIENT)
    fun provideMainOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        acceptLanguageInterceptor: AcceptLanguageInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(acceptLanguageInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    @Named(value = MODULE_REFRESH_HTTP_CLIENT)
    fun provideRefreshOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        acceptLanguageInterceptor: AcceptLanguageInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(acceptLanguageInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named(value = MODULE_USER_SERVICE_RETROFIT)
    fun provideUserServiceRetrofit(
        @Named(value = MODULE_MAIN_HTTP_CLIENT) okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConstants.USER_SERVICE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    @Named(value = MODULE_PEDALPAL_SERVICE_RETROFIT)
    fun providePedalPalServiceRetrofit(
        @Named(value = MODULE_MAIN_HTTP_CLIENT) okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConstants.PEDALPAL_SERVICE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    @Named(value = MODULE_REFRESH_RETROFIT)
    fun provideRefreshRetrofit(
        @Named(value = MODULE_REFRESH_HTTP_CLIENT) okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConstants.USER_SERVICE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthRefreshApi(
        @Named(value = MODULE_REFRESH_RETROFIT) retrofit: Retrofit
    ): AuthRefreshApi {
        return retrofit.create(AuthRefreshApi::class.java)
    }
}
