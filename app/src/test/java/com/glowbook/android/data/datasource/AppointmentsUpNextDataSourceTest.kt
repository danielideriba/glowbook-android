package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.AppointmentsUpNextApiService
import com.glowbook.android.data.remote.model.AppointmentUpNextResponse
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

class AppointmentsUpNextDataSourceTest {

    private val apiService: AppointmentsUpNextApiService = mockk()
    private lateinit var dataSource: AppointmentsUpNextDataSource

    @Before
    fun setUp() {
        dataSource = AppointmentsUpNextDataSourceImpl(apiService)
    }

    @Test
    fun `fetchAppointmentsUpNext returns upcoming appointments from api`() = runTest {
        val appointments = listOf(upNextAppointment(id = 1), upNextAppointment(id = 2))
        coEvery { apiService.appointmentsUpNext() } returns Response.success(appointments)

        val response = dataSource.fetchAppointmentsUpNext()

        assertTrue(response.isSuccessful)
        assertEquals(appointments, response.body())
    }

    @Test
    fun `fetchAppointmentsUpNext returns the exact api response`() = runTest {
        val apiResponse = Response.success(listOf(upNextAppointment(id = 1)))
        coEvery { apiService.appointmentsUpNext() } returns apiResponse

        val response = dataSource.fetchAppointmentsUpNext()

        assertSame(apiResponse, response)
    }

    @Test
    fun `fetchAppointmentsUpNext returns empty list when there are no upcoming appointments`() = runTest {
        coEvery { apiService.appointmentsUpNext() } returns Response.success(emptyList())

        val response = dataSource.fetchAppointmentsUpNext()

        assertTrue(response.isSuccessful)
        assertEquals(emptyList<AppointmentUpNextResponse>(), response.body())
    }

    @Test
    fun `fetchAppointmentsUpNext returns error response when api fails`() = runTest {
        val errorBody = """{"message":"internal error"}"""
            .toResponseBody("application/json".toMediaType())
        coEvery { apiService.appointmentsUpNext() } returns Response.error(500, errorBody)

        val response = dataSource.fetchAppointmentsUpNext()

        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
        assertNull(response.body())
    }

    @Test(expected = IOException::class)
    fun `fetchAppointmentsUpNext propagates network exceptions`() = runTest {
        coEvery { apiService.appointmentsUpNext() } throws IOException("no network")

        dataSource.fetchAppointmentsUpNext()
    }

    @Test
    fun `fetchAppointmentsUpNext calls api ordered by date and time once`() = runTest {
        coEvery { apiService.appointmentsUpNext(any(), any()) } returns Response.success(emptyList())

        dataSource.fetchAppointmentsUpNext()

        coVerify(exactly = 1) {
            apiService.appointmentsUpNext(select = "*", order = "data.asc,horario.asc")
        }
        confirmVerified(apiService)
    }

    private fun upNextAppointment(id: Long) = AppointmentUpNextResponse(
        id = id,
        idServicos = 1,
        data = "2026-10-30",
        horario = "14:00:00",
        status = "confirmado",
        profissionalId = 1,
        profissional = "Camila Rocha",
        avaliacao = 4.9,
        categoria = "Cabelo"
    )
}
