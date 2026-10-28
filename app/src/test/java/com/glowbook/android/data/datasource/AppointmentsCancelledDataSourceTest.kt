package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.AppointmentsHistoryApiService
import com.glowbook.android.data.remote.model.AppointmentsHistoryResponse
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

class AppointmentsCancelledDataSourceTest {

    private val apiService: AppointmentsHistoryApiService = mockk()
    private lateinit var dataSource: AppointmentsCancelledDataSource

    @Before
    fun setUp() {
        dataSource = AppointmentsCancelledDataSourceImpl(apiService)
    }

    @Test
    fun `fetchAppointmentsCancelled returns cancelled appointments from api`() = runTest {
        val appointments = listOf(cancelledAppointment(id = 1), cancelledAppointment(id = 2))
        coEvery { apiService.appointmentsCancelled() } returns Response.success(appointments)

        val response = dataSource.fetchAppointmentsCancelled()

        assertTrue(response.isSuccessful)
        assertEquals(appointments, response.body())
        assertTrue(response.body().orEmpty().all { it.status == "cancelado" })
    }

    @Test
    fun `fetchAppointmentsCancelled returns the exact api response`() = runTest {
        val apiResponse = Response.success(listOf(cancelledAppointment(id = 1)))
        coEvery { apiService.appointmentsCancelled() } returns apiResponse

        val response = dataSource.fetchAppointmentsCancelled()

        assertSame(apiResponse, response)
    }

    @Test
    fun `fetchAppointmentsCancelled returns empty list when there are no cancelled appointments`() = runTest {
        coEvery { apiService.appointmentsCancelled() } returns Response.success(emptyList())

        val response = dataSource.fetchAppointmentsCancelled()

        assertTrue(response.isSuccessful)
        assertEquals(emptyList<AppointmentsHistoryResponse>(), response.body())
    }

    @Test
    fun `fetchAppointmentsCancelled returns error response when api fails`() = runTest {
        val errorBody = """{"code":"PGRST205","message":"not found"}"""
            .toResponseBody("application/json".toMediaType())
        coEvery { apiService.appointmentsCancelled() } returns Response.error(404, errorBody)

        val response = dataSource.fetchAppointmentsCancelled()

        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
        assertNull(response.body())
    }

    @Test(expected = IOException::class)
    fun `fetchAppointmentsCancelled propagates network exceptions`() = runTest {
        coEvery { apiService.appointmentsCancelled() } throws IOException("no network")

        dataSource.fetchAppointmentsCancelled()
    }

    @Test
    fun `fetchAppointmentsCancelled calls api with default cancelled filter once`() = runTest {
        coEvery { apiService.appointmentsCancelled(any(), any()) } returns Response.success(emptyList())

        dataSource.fetchAppointmentsCancelled()

        coVerify(exactly = 1) { apiService.appointmentsCancelled(select = "*", status = "eq.cancelado") }
        confirmVerified(apiService)
    }

    private fun cancelledAppointment(id: Long) = AppointmentsHistoryResponse(
        id = id,
        data = "2026-10-30",
        horario = "14:00:00",
        status = "cancelado",
        jaPassou = false,
        profissionalId = 1,
        profissional = "Camila Rocha",
        avaliacao = 4.9,
        categoria = "Cabelo"
    )
}
