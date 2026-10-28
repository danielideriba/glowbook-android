package com.glowbook.android.di

import android.content.Context
import com.glowbook.android.data.remote.ApiClientConstants.API_KEY
import com.glowbook.android.data.remote.ApiClientConstants.BEARER_ANON
import com.glowbook.android.data.remote.ApiClientConstants.CONTENT_TYPE_JSON
import com.glowbook.android.data.remote.ApiClientConstants.HEADER_PARAM_API_KEY
import com.glowbook.android.data.remote.ApiClientConstants.HEADER_PARAM_AUTHORIZATION
import com.glowbook.android.data.remote.ApiClientConstants.HEADER_PARAM_CONTENT_TYPE
import com.glowbook.android.data.remote.ApiPath.BASE_URL
import com.glowbook.android.data.remote.AppointmentsApiService
import com.glowbook.android.data.remote.AppointmentsHistoryApiService
import com.glowbook.android.data.remote.AppointmentsUpNextApiService
import com.glowbook.android.data.remote.CategoriesApiServices
import com.glowbook.android.data.remote.CreateAppointmentApiService
import com.glowbook.android.data.remote.ProfessionalsApiService
import com.glowbook.android.data.remote.ServicesApiService
import com.glowbook.android.utils.NetworkConstants.CONNECTION_TIMEOUT
import com.glowbook.android.utils.NetworkConstants.READ_TIMEOUT
import com.glowbook.android.utils.NetworkConstants.WRITE_TIMEOUT
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideHttpInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)

        return loggingInterceptor
    }

    @Singleton
    @Provides
    fun provideCache(context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun provideAuthInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader(HEADER_PARAM_API_KEY, API_KEY)
            .build()
        chain.proceed(request)
    }

    //Interceptor para POST do supabase
    @Singleton
    @Provides
    @BearerInterceptor
    fun provideBearerInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header(HEADER_PARAM_AUTHORIZATION, BEARER_ANON)
            .header(HEADER_PARAM_CONTENT_TYPE, CONTENT_TYPE_JSON)
            .build()
        chain.proceed(request)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
        cache: Cache
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .cache(cache)
            .connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    @CreateAppointmentClient
    fun provideCreateAppointmentOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
        @BearerInterceptor bearerInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(bearerInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    @CreateAppointmentClient
    fun provideCreateAppointmentRetrofit(
        @CreateAppointmentClient okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideProfessionalsApiService(retrofit: Retrofit): ProfessionalsApiService =
        retrofit.create(ProfessionalsApiService::class.java)

    @Singleton
    @Provides
    fun provideCategoriesApiServices(retrofit: Retrofit): CategoriesApiServices =
        retrofit.create(CategoriesApiServices::class.java)

    @Singleton
    @Provides
    fun provideAppointmentsUpNextApiService(retrofit: Retrofit): AppointmentsUpNextApiService =
        retrofit.create(AppointmentsUpNextApiService::class.java)

    @Singleton
    @Provides
    fun provideAppointmentsHistoryApiService(retrofit: Retrofit): AppointmentsHistoryApiService =
        retrofit.create(AppointmentsHistoryApiService::class.java)

    @Singleton
    @Provides
    fun provideServicesApiService(retrofit: Retrofit): ServicesApiService =
        retrofit.create(ServicesApiService::class.java)

    @Singleton
    @Provides
    fun provideAppointmentsApiService(retrofit: Retrofit): AppointmentsApiService =
        retrofit.create(AppointmentsApiService::class.java)

    @Singleton
    @Provides
    fun provideCreateAppointmentApiService(
        @CreateAppointmentClient retrofit: Retrofit
    ): CreateAppointmentApiService =
        retrofit.create(CreateAppointmentApiService::class.java)
}
