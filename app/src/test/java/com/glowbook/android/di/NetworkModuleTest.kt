package com.glowbook.android.di

import android.content.Context
import com.glowbook.android.data.remote.ApiClientConstants
import com.glowbook.android.data.remote.ApiPath
import com.glowbook.android.data.remote.AppointmentsApiService
import com.glowbook.android.data.remote.AppointmentsHistoryApiService
import com.glowbook.android.data.remote.AppointmentsUpNextApiService
import com.glowbook.android.data.remote.CategoriesApiServices
import com.glowbook.android.data.remote.CreateAppointmentApiService
import com.glowbook.android.data.remote.ProfessionalsApiService
import com.glowbook.android.data.remote.ServicesApiService
import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import com.glowbook.android.testutils.FakeApiInterceptor
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.lang.reflect.Proxy
import java.util.Date

class NetworkModuleTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val logging = NetworkModule.provideHttpInterceptor()
    private val auth = NetworkModule.provideAuthInterceptor()
    private val bearer = NetworkModule.provideBearerInterceptor()
    private val converter = NetworkModule.provideGsonConverterFactory(NetworkModule.provideGson())

    private fun cache(): Cache {
        val context: Context = mockk { every { cacheDir } returns tempFolder.root }
        return NetworkModule.provideCache(context)
    }

    /** Runs [interceptors] followed by a fake server and returns the request that reached it. */
    private fun requestAfter(vararg interceptors: Interceptor): Request {
        val fake = FakeApiInterceptor()
        val client = OkHttpClient.Builder()
            .apply { interceptors.forEach { addInterceptor(it) } }
            .addInterceptor(fake)
            .build()
        client.newCall(Request.Builder().url(ApiPath.BASE_URL + "servicos").build()).execute().close()
        return fake.lastRequest
    }

    @Test
    fun `logging interceptor is disabled`() {
        assertEquals(HttpLoggingInterceptor.Level.NONE, logging.level)
    }

    @Test
    fun `cache uses context cache dir with 10 MB`() {
        val cache = cache()

        assertEquals(tempFolder.root, cache.directory)
        assertEquals(10L * 1024 * 1024, cache.maxSize())
    }

    @Test
    fun `gson uses supabase timestamp format`() {
        val gson = NetworkModule.provideGson()

        val date = gson.fromJson("\"2026-10-30T14:00:00.000Z\"", Date::class.java)

        assertEquals("\"2026-10-30T14:00:00.000Z\"", gson.toJson(date))
    }

    @Test
    fun `auth interceptor adds api key header`() {
        val request = requestAfter(auth)

        assertEquals(ApiClientConstants.API_KEY, request.header(ApiClientConstants.HEADER_PARAM_API_KEY))
        assertNull(request.header(ApiClientConstants.HEADER_PARAM_AUTHORIZATION))
    }

    @Test
    fun `bearer interceptor adds authorization and json content type`() {
        val request = requestAfter(bearer)

        assertEquals(ApiClientConstants.BEARER_ANON, request.header(ApiClientConstants.HEADER_PARAM_AUTHORIZATION))
        assertTrue(ApiClientConstants.BEARER_ANON.startsWith("Bearer "))
        assertEquals(ApiClientConstants.CONTENT_TYPE_JSON, request.header(ApiClientConstants.HEADER_PARAM_CONTENT_TYPE))
    }

    @Test
    fun `default client uses auth and logging interceptors with cache and timeouts`() {
        val cache = cache()
        val client = NetworkModule.provideOkHttpClient(logging, auth, cache)

        assertEquals(listOf(auth, logging), client.interceptors)
        assertSame(cache, client.cache)
        assertEquals(45_000, client.connectTimeoutMillis)
        assertEquals(45_000, client.readTimeoutMillis)
        assertEquals(45_000, client.writeTimeoutMillis)
    }

    @Test
    fun `create appointment client adds bearer after auth and has no cache`() {
        val client = NetworkModule.provideCreateAppointmentOkHttpClient(logging, auth, bearer)

        assertEquals(listOf(auth, bearer, logging), client.interceptors)
        assertNull(client.cache)
        assertEquals(45_000, client.connectTimeoutMillis)
        assertEquals(45_000, client.readTimeoutMillis)
        assertEquals(45_000, client.writeTimeoutMillis)
    }

    @Test
    fun `retrofit instances point to supabase rest url`() {
        val okHttp = OkHttpClient()

        val retrofit = NetworkModule.provideRetrofit(okHttp, converter)
        val createRetrofit = NetworkModule.provideCreateAppointmentRetrofit(okHttp, converter)

        assertEquals(ApiPath.BASE_URL, retrofit.baseUrl().toString())
        assertEquals(ApiPath.BASE_URL, createRetrofit.baseUrl().toString())
        assertSame(converter, retrofit.converterFactories().first { it === converter })
    }

    @Test
    fun `providers create retrofit api services`() {
        val retrofit = NetworkModule.provideRetrofit(OkHttpClient(), converter)

        val services = listOf(
            NetworkModule.provideProfessionalsApiService(retrofit) to ProfessionalsApiService::class.java,
            NetworkModule.provideCategoriesApiServices(retrofit) to CategoriesApiServices::class.java,
            NetworkModule.provideAppointmentsUpNextApiService(retrofit) to AppointmentsUpNextApiService::class.java,
            NetworkModule.provideAppointmentsHistoryApiService(retrofit) to AppointmentsHistoryApiService::class.java,
            NetworkModule.provideServicesApiService(retrofit) to ServicesApiService::class.java,
            NetworkModule.provideAppointmentsApiService(retrofit) to AppointmentsApiService::class.java,
            NetworkModule.provideCreateAppointmentApiService(retrofit) to CreateAppointmentApiService::class.java
        )

        services.forEach { (service, type) ->
            assertTrue(type.isInstance(service))
            assertTrue(Proxy.isProxyClass(service.javaClass))
        }
    }

    @Test
    fun `create appointment request goes out with api key bearer and json`() = runTest {
        val fake = FakeApiInterceptor(responseBody = "42")
        val client = NetworkModule.provideCreateAppointmentOkHttpClient(logging, auth, bearer)
            .newBuilder()
            .addInterceptor(fake)
            .build()
        val apiService = NetworkModule.provideCreateAppointmentApiService(
            NetworkModule.provideCreateAppointmentRetrofit(client, converter)
        )

        val response = apiService.createAppointment(CreateAppointmentRequest("2026-10-30", "14:00", 1, listOf(1)))

        val request = fake.lastRequest
        assertEquals(42L, response.body())
        assertEquals(ApiClientConstants.API_KEY, request.header(ApiClientConstants.HEADER_PARAM_API_KEY))
        assertEquals(ApiClientConstants.BEARER_ANON, request.header(ApiClientConstants.HEADER_PARAM_AUTHORIZATION))
        assertEquals(ApiClientConstants.CONTENT_TYPE_JSON, request.header(ApiClientConstants.HEADER_PARAM_CONTENT_TYPE))
    }

    @Test
    fun `read requests go out with api key only`() = runTest {
        val fake = FakeApiInterceptor()
        val client = NetworkModule.provideOkHttpClient(logging, auth, cache())
            .newBuilder()
            .addInterceptor(fake)
            .build()
        val apiService = NetworkModule.provideServicesApiService(NetworkModule.provideRetrofit(client, converter))

        apiService.services()

        val request = fake.lastRequest
        assertEquals(ApiClientConstants.API_KEY, request.header(ApiClientConstants.HEADER_PARAM_API_KEY))
        assertNull(request.header(ApiClientConstants.HEADER_PARAM_AUTHORIZATION))
    }
}
