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

class AppointmentsHistoryDataSourceTest {

    private val apiService: AppointmentsHistoryApiService = mockk()
    private lateinit var dataSource: AppointmentsHistoryDataSource

    @Before
    fun setUp() {
        dataSource = AppointmentsHistoryDataSourceImpl(apiService)
    }

    @Test
    fun `fetchAppointmentsHistory returns appointments history from api`() = runTest {
        val appointments = listOf(historyAppointment(id = 1), historyAppointment(id = 2))
        coEvery { apiService.appointmentsHistory() } returns Response.success(appointments)

        val response = dataSource.fetchAppointmentsHistory()

        assertTrue(response.isSuccessful)
        assertEquals(appointments, response.body())
    }

    @Test
    fun `fetchAppointmentsHistory returns the exact api response`() = runTest {
        val apiResponse = Response.success(listOf(historyAppointment(id = 1)))
        coEvery { apiService.appointmentsHistory() } returns apiResponse

        val response = dataSource.fetchAppointmentsHistory()

        assertSame(apiResponse, response)
    }

    @Test
    fun `fetchAppointmentsHistory returns empty list when there is no history`() = runTest {
        coEvery { apiService.appointmentsHistory() } returns Response.success(emptyList())

        val response = dataSource.fetchAppointmentsHistory()

        assertTrue(response.isSuccessful)
        assertEquals(emptyList<AppointmentsHistoryResponse>(), response.body())
    }

    @Test
    fun `fetchAppointmentsHistory returns error response when api fails`() = runTest {
        val errorBody = """{"code":"PGRST205","message":"not found"}"""
            .toResponseBody("application/json".toMediaType())
        coEvery { apiService.appointmentsHistory() } returns Response.error(404, errorBody)

        val response = dataSource.fetchAppointmentsHistory()

        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
        assertNull(response.body())
    }

    @Test(expected = IOException::class)
    fun `fetchAppointmentsHistory propagates network exceptions`() = runTest {
        coEvery { apiService.appointmentsHistory() } throws IOException("no network")

        dataSource.fetchAppointmentsHistory()
    }

    @Test
    fun `fetchAppointmentsHistory calls api with default select once`() = runTest {
        coEvery { apiService.appointmentsHistory(any()) } returns Response.success(emptyList())

        dataSource.fetchAppointmentsHistory()

        coVerify(exactly = 1) { apiService.appointmentsHistory(select = "*") }
        confirmVerified(apiService)
    }

    private fun historyAppointment(id: Long) = AppointmentsHistoryResponse(
        id = id,
        data = "2026-10-20",
        horario = "10:00:00",
        status = "concluido",
        jaPassou = true,
        profissionalId = 1,
        profissional = "Camila Rocha",
        avaliacao = 4.9,
        categoria = "Cabelo"
    )
}
