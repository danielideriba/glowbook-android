package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.CreateAppointmentApiService
import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class CreateAppointmentDataSourceTest {

    private val apiService: CreateAppointmentApiService = mockk()
    private lateinit var dataSource: CreateAppointmentDataSource

    private val request = CreateAppointmentRequest(
        data = "2026-10-30",
        horario = "14:00",
        profissionalId = 1,
        servicos = listOf(1, 2)
    )

    @Before
    fun setUp() {
        dataSource = CreateAppointmentDataSourceImpl(apiService)
    }

    @Test
    fun `createAppointment returns created appointment id from api`() = runTest {
        coEvery { apiService.createAppointment(request) } returns Response.success(42L)

        val response = dataSource.createAppointment(request)

        assertTrue(response.isSuccessful)
        assertEquals(42L, response.body())
    }

    @Test
    fun `createAppointment returns the exact api response`() = runTest {
        val apiResponse = Response.success(42L)
        coEvery { apiService.createAppointment(request) } returns apiResponse

        val response = dataSource.createAppointment(request)

        assertSame(apiResponse, response)
    }

    @Test
    fun `createAppointment sends the request body unchanged once`() = runTest {
        coEvery { apiService.createAppointment(any()) } returns Response.success(42L)

        dataSource.createAppointment(request)

        coVerify(exactly = 1) {
            apiService.createAppointment(
                CreateAppointmentRequest(
                    data = "2026-10-30",
                    horario = "14:00",
                    profissionalId = 1,
                    servicos = listOf(1, 2)
                )
            )
        }
        confirmVerified(apiService)
    }

    @Test
    fun `createAppointment returns error response when rpc rejects the request`() = runTest {
        val errorBody = """{"code":"22007","message":"invalid input syntax for type date"}"""
            .toResponseBody("application/json".toMediaType())
        coEvery { apiService.createAppointment(request) } returns Response.error(400, errorBody)

        val response = dataSource.createAppointment(request)

        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())
        assertNull(response.body())
    }

    @Test(expected = IOException::class)
    fun `createAppointment propagates network exceptions`() = runTest {
        coEvery { apiService.createAppointment(request) } throws IOException("no network")

        dataSource.createAppointment(request)
    }
}
