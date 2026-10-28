package com.glowbook.android.di

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.glowbook.android.data.datasource.AppointmentsCancelledDataSource
import com.glowbook.android.data.datasource.AppointmentsCancelledDataSourceImpl
import com.glowbook.android.data.datasource.AppointmentsDataSource
import com.glowbook.android.data.datasource.AppointmentsDataSourceImpl
import com.glowbook.android.data.datasource.AppointmentsHistoryDataSource
import com.glowbook.android.data.datasource.AppointmentsHistoryDataSourceImpl
import com.glowbook.android.data.datasource.AppointmentsUpNextDataSource
import com.glowbook.android.data.datasource.AppointmentsUpNextDataSourceImpl
import com.glowbook.android.data.datasource.CategoriesDataSource
import com.glowbook.android.data.datasource.CategoriesDataSourceImpl
import com.glowbook.android.data.datasource.CreateAppointmentDataSource
import com.glowbook.android.data.datasource.CreateAppointmentDataSourceImpl
import com.glowbook.android.data.datasource.ProfessionalsDataSource
import com.glowbook.android.data.datasource.ProfessionalsDataSourceImpl
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.datasource.ServicesDataSourceImpl
import com.glowbook.android.data.remote.ApiClientConstants
import com.glowbook.android.data.remote.ApiPath
import com.glowbook.android.data.remote.AppointmentsApiService
import com.glowbook.android.data.remote.AppointmentsHistoryApiService
import com.glowbook.android.data.remote.AppointmentsUpNextApiService
import com.glowbook.android.data.remote.CategoriesApiServices
import com.glowbook.android.data.remote.CreateAppointmentApiService
import com.glowbook.android.data.remote.ProfessionalsApiService
import com.glowbook.android.data.remote.ServicesApiService
import com.glowbook.android.data.repository.AppointmentsCancelledRepositoryImpl
import com.glowbook.android.data.repository.AppointmentsHistoryRepositoryImpl
import com.glowbook.android.data.repository.AppointmentsUpNextRepositoryImpl
import com.glowbook.android.data.repository.CategoriesRepositoryImpl
import com.glowbook.android.data.repository.CreateAppointmentRepositoryImpl
import com.glowbook.android.data.repository.ProfessionalCalendarRepositoryImpl
import com.glowbook.android.data.repository.ProfessionalServicesRepositoryImpl
import com.glowbook.android.data.repository.ProfessionalsRepositoryImpl
import com.glowbook.android.domain.repository.AppointmentsCancelledRepository
import com.glowbook.android.domain.repository.AppointmentsHistoryRepository
import com.glowbook.android.domain.repository.AppointmentsUpNextRepository
import com.glowbook.android.domain.repository.CategoriesRepository
import com.glowbook.android.domain.repository.CreateAppointmentRepository
import com.glowbook.android.domain.repository.ProfessionalCalendarRepository
import com.glowbook.android.domain.repository.ProfessionalServicesRepository
import com.glowbook.android.domain.repository.ProfessionalsRepository
import com.glowbook.android.testutils.FakeSupabase
import com.glowbook.android.utils.DefaultDispatcherProvider
import com.glowbook.android.utils.DispatchersProvider
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Provider

/** Builds the real Hilt graph (NetworkModule, RepositoryModule, AppModule) on the device. */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HiltGraphTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject @ApplicationContext lateinit var appContext: Context
    @Inject lateinit var injectedContext: Context
    @Inject lateinit var gson: Gson
    @Inject lateinit var cache: Cache
    @Inject lateinit var logging: HttpLoggingInterceptor
    @Inject lateinit var authInterceptor: Interceptor
    @Inject @BearerInterceptor lateinit var bearerInterceptor: Interceptor
    @Inject lateinit var okHttpClient: Provider<OkHttpClient>
    @Inject @CreateAppointmentClient lateinit var createAppointmentClient: Provider<OkHttpClient>
    @Inject lateinit var retrofit: Provider<Retrofit>
    @Inject @CreateAppointmentClient lateinit var createAppointmentRetrofit: Retrofit

    @Inject lateinit var professionalsApiService: ProfessionalsApiService
    @Inject lateinit var categoriesApiServices: CategoriesApiServices
    @Inject lateinit var appointmentsUpNextApiService: AppointmentsUpNextApiService
    @Inject lateinit var appointmentsHistoryApiService: AppointmentsHistoryApiService
    @Inject lateinit var servicesApiService: ServicesApiService
    @Inject lateinit var appointmentsApiService: AppointmentsApiService
    @Inject lateinit var createAppointmentApiService: Provider<CreateAppointmentApiService>

    @Inject lateinit var professionalsDataSource: ProfessionalsDataSource
    @Inject lateinit var categoriesDataSource: CategoriesDataSource
    @Inject lateinit var appointmentsUpNextDataSource: AppointmentsUpNextDataSource
    @Inject lateinit var appointmentsHistoryDataSource: AppointmentsHistoryDataSource
    @Inject lateinit var appointmentsCancelledDataSource: AppointmentsCancelledDataSource
    @Inject lateinit var servicesDataSource: ServicesDataSource
    @Inject lateinit var appointmentsDataSource: AppointmentsDataSource
    @Inject lateinit var createAppointmentDataSource: CreateAppointmentDataSource

    @Inject lateinit var professionalsRepository: Provider<ProfessionalsRepository>
    @Inject lateinit var categoriesRepository: CategoriesRepository
    @Inject lateinit var appointmentsUpNextRepository: AppointmentsUpNextRepository
    @Inject lateinit var appointmentsHistoryRepository: AppointmentsHistoryRepository
    @Inject lateinit var appointmentsCancelledRepository: AppointmentsCancelledRepository
    @Inject lateinit var professionalServicesRepository: ProfessionalServicesRepository
    @Inject lateinit var professionalCalendarRepository: ProfessionalCalendarRepository
    @Inject lateinit var createAppointmentRepository: CreateAppointmentRepository

    @Inject lateinit var dispatchersProvider: DispatchersProvider

    @Before
    fun setUp() {
        hiltRule.inject()
        FakeSupabase.reset()
    }

    /** Sends a request through [interceptor] and returns what reached the (fake) server. */
    private fun requestThrough(interceptor: Interceptor): FakeSupabase.RecordedRequest {
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(FakeSupabase)
            .build()
            .newCall(Request.Builder().url(ApiPath.BASE_URL + "categorias").build())
            .execute()
            .close()
        return FakeSupabase.requests.last()
    }

    @Test
    fun appModule_providesApplicationContext() {
        assertSame(appContext, injectedContext)
    }

    @Test
    fun networkModule_cacheLivesInAppCacheDir() {
        assertEquals(appContext.cacheDir, cache.directory)
        assertEquals(10L * 1024 * 1024, cache.maxSize())
    }

    @Test
    fun networkModule_loggingIsDisabled() {
        assertEquals(HttpLoggingInterceptor.Level.NONE, logging.level)
    }

    @Test
    fun networkModule_authInterceptorAddsApiKeyOnly() {
        val request = requestThrough(authInterceptor)

        assertEquals(ApiClientConstants.API_KEY, request.headers[ApiClientConstants.HEADER_PARAM_API_KEY])
        assertNull(request.headers[ApiClientConstants.HEADER_PARAM_AUTHORIZATION])
    }

    @Test
    fun networkModule_bearerInterceptorAddsAuthorizationAndContentType() {
        val request = requestThrough(bearerInterceptor)

        assertEquals(ApiClientConstants.BEARER_ANON, request.headers[ApiClientConstants.HEADER_PARAM_AUTHORIZATION])
        assertEquals(ApiClientConstants.CONTENT_TYPE_JSON, request.headers[ApiClientConstants.HEADER_PARAM_CONTENT_TYPE])
    }

    @Test
    fun networkModule_defaultClientUsesInjectedInterceptorsAndCache() {
        val client = okHttpClient.get()

        assertEquals(listOf(authInterceptor, logging), client.interceptors)
        assertSame(cache, client.cache)
        assertEquals(45_000, client.connectTimeoutMillis)
        assertEquals(45_000, client.readTimeoutMillis)
        assertEquals(45_000, client.writeTimeoutMillis)
    }

    @Test
    fun networkModule_createAppointmentClientAddsBearerWithoutCache() {
        val client = createAppointmentClient.get()

        assertEquals(listOf(authInterceptor, bearerInterceptor, logging), client.interceptors)
        assertNull(client.cache)
    }

    @Test
    fun networkModule_qualifiedClientsAreDifferentInstances() {
        assertTrue(okHttpClient.get() !== createAppointmentClient.get())
        assertTrue(retrofit.get() !== createAppointmentRetrofit)
        assertSame(createAppointmentClient.get(), createAppointmentRetrofit.callFactory())
        assertSame(okHttpClient.get(), retrofit.get().callFactory())
    }

    @Test
    fun networkModule_retrofitPointsToSupabase() {
        assertEquals(ApiPath.BASE_URL, retrofit.get().baseUrl().toString())
        assertEquals(ApiPath.BASE_URL, createAppointmentRetrofit.baseUrl().toString())
    }

    @Test
    fun networkModule_providesSingletons() {
        assertSame(okHttpClient.get(), okHttpClient.get())
        assertSame(createAppointmentClient.get(), createAppointmentClient.get())
        assertSame(retrofit.get(), retrofit.get())
        assertSame(createAppointmentApiService.get(), createAppointmentApiService.get())
    }

    @Test
    fun networkModule_providesAllApiServices() {
        listOf(
            professionalsApiService,
            categoriesApiServices,
            appointmentsUpNextApiService,
            appointmentsHistoryApiService,
            servicesApiService,
            appointmentsApiService,
            createAppointmentApiService.get()
        ).forEach { assertTrue(java.lang.reflect.Proxy.isProxyClass(it.javaClass)) }
    }

    @Test
    fun networkModule_createAppointmentServiceUsesBearerClient() {
        val client = createAppointmentClient.get()
        val service = createAppointmentRetrofit.create(CreateAppointmentApiService::class.java)

        assertTrue(client.interceptors.contains(bearerInterceptor))
        assertTrue(service.javaClass.interfaces.contains(CreateAppointmentApiService::class.java))
    }

    @Test
    fun repositoryModule_bindsDataSourceImplementations() {
        assertTrue(professionalsDataSource is ProfessionalsDataSourceImpl)
        assertTrue(categoriesDataSource is CategoriesDataSourceImpl)
        assertTrue(appointmentsUpNextDataSource is AppointmentsUpNextDataSourceImpl)
        assertTrue(appointmentsHistoryDataSource is AppointmentsHistoryDataSourceImpl)
        assertTrue(appointmentsCancelledDataSource is AppointmentsCancelledDataSourceImpl)
        assertTrue(servicesDataSource is ServicesDataSourceImpl)
        assertTrue(appointmentsDataSource is AppointmentsDataSourceImpl)
        assertTrue(createAppointmentDataSource is CreateAppointmentDataSourceImpl)
    }

    @Test
    fun repositoryModule_bindsRepositoryImplementations() {
        assertTrue(professionalsRepository.get() is ProfessionalsRepositoryImpl)
        assertTrue(categoriesRepository is CategoriesRepositoryImpl)
        assertTrue(appointmentsUpNextRepository is AppointmentsUpNextRepositoryImpl)
        assertTrue(appointmentsHistoryRepository is AppointmentsHistoryRepositoryImpl)
        assertTrue(appointmentsCancelledRepository is AppointmentsCancelledRepositoryImpl)
        assertTrue(professionalServicesRepository is ProfessionalServicesRepositoryImpl)
        assertTrue(professionalCalendarRepository is ProfessionalCalendarRepositoryImpl)
        assertTrue(createAppointmentRepository is CreateAppointmentRepositoryImpl)
    }

    @Test
    fun repositoryModule_repositoriesAreSingletons() {
        assertSame(professionalsRepository.get(), professionalsRepository.get())
    }

    @Test
    fun repositoryModule_bindsDefaultDispatcherProvider() {
        assertTrue(dispatchersProvider is DefaultDispatcherProvider)
    }
}
