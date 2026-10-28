package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.AppointmentsApiService
import com.glowbook.android.data.remote.model.AppointmentsResponse
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

class AppointmentsDataSourceTest {

    private val apiService: AppointmentsApiService = mockk()
    private lateinit var dataSource: AppointmentsDataSource

    @Before
    fun setUp() {
        dataSource = AppointmentsDataSourceImpl(apiService)
    }

    @Test
    fun `fetchProfessionalAppointments returns professional appointments from api`() = runTest {
        val appointments = listOf(appointment(id = 1), appointment(id = 2))
        coEvery { apiService.appointments(any(), any(), any(), any(), any()) } returns Response.success(appointments)

        val response = dataSource.fetchProfessionalAppointments(PROFESSIONAL_ID, START_DATE, END_DATE)

        assertTrue(response.isSuccessful)
        assertEquals(appointments, response.body())
    }

    @Test
    fun `fetchProfessionalAppointments returns the exact api response`() = runTest {
        val apiResponse = Response.success(listOf(appointment(id = 1)))
        coEvery { apiService.appointments(any(), any(), any(), any(), any()) } returns apiResponse

        val response = dataSource.fetchProfessionalAppointments(PROFESSIONAL_ID, START_DATE, END_DATE)

        assertSame(apiResponse, response)
    }

    @Test
    fun `fetchProfessionalAppointments filters by professional and date range`() = runTest {
        coEvery { apiService.appointments(any(), any(), any(), any(), any()) } returns Response.success(emptyList())

        dataSource.fetchProfessionalAppointments(PROFESSIONAL_ID, START_DATE, END_DATE)

        coVerify(exactly = 1) {
            apiService.appointments(
                select = "*",
                profissionalId = "eq.1",
                status = "neq.cancelado",
                data = listOf("gte.2026-10-27", "lte.2026-11-02"),
                order = "data.asc,horario.asc"
            )
        }
        confirmVerified(apiService)
    }

    @Test
    fun `fetchProfessionalAppointments returns empty list when professional has no bookings`() = runTest {
        coEvery { apiService.appointments(any(), any(), any(), any(), any()) } returns Response.success(emptyList())

        val response = dataSource.fetchProfessionalAppointments(PROFESSIONAL_ID, START_DATE, END_DATE)

        assertTrue(response.isSuccessful)
        assertEquals(emptyList<AppointmentsResponse>(), response.body())
    }

    @Test
    fun `fetchProfessionalAppointments returns error response when api fails`() = runTest {
        val errorBody = """{"code":"PGRST205","message":"not found"}"""
            .toResponseBody("application/json".toMediaType())
        coEvery { apiService.appointments(any(), any(), any(), any(), any()) } returns Response.error(404, errorBody)

        val response = dataSource.fetchProfessionalAppointments(PROFESSIONAL_ID, START_DATE, END_DATE)

        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
        assertNull(response.body())
    }

    @Test(expected = IOException::class)
    fun `fetchProfessionalAppointments propagates network exceptions`() = runTest {
        coEvery { apiService.appointments(any(), any(), any(), any(), any()) } throws IOException("no network")

        dataSource.fetchProfessionalAppointments(PROFESSIONAL_ID, START_DATE, END_DATE)
    }

    private fun appointment(id: Long) = AppointmentsResponse(
        id = id,
        data = "2026-10-30",
        horario = "11:00:00",
        status = "confirmado",
        profissionalId = PROFESSIONAL_ID
    )

    private companion object {
        const val PROFESSIONAL_ID = 1L
        const val START_DATE = "2026-10-27"
        const val END_DATE = "2026-11-02"
    }
}
