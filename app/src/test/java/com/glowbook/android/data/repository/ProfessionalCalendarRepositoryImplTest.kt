package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.AppointmentsDataSource
import com.glowbook.android.data.datasource.ProfessionalsDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.remote.model.ProfessionalCalendarResponse
import com.glowbook.android.domain.repository.ProfessionalCalendarRepository
import com.glowbook.android.testutils.TestDispatchersProvider
import com.glowbook.android.utils.ApiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ProfessionalCalendarRepositoryImplTest {

    private val professionalsDataSource: ProfessionalsDataSource = mockk()
    private val servicesDataSource: ServicesDataSource = mockk()
    private val appointmentsDataSource: AppointmentsDataSource = mockk()
    private val repository: ProfessionalCalendarRepository = ProfessionalCalendarRepositoryImpl(
        professionalsDataSource,
        servicesDataSource,
        appointmentsDataSource,
        TestDispatchersProvider()
    )

    private fun fetch(serviceIds: List<Long> = listOf(1, 2)) =
        repository.fetchProfessionalCalendar(PROFESSIONAL_ID, serviceIds, START_DATE, END_DATE)

    private fun givenProfessionalFound() {
        coEvery { professionalsDataSource.fetchProfessionalById(PROFESSIONAL_ID) } returns
            Response.success(listOf(professional(PROFESSIONAL_ID)))
    }

    @Test
    fun `emits professional with selected services and booked appointments`() = runTest {
        val services = listOf(service(1), service(2))
        val bookings = listOf(booking(10, "11:00:00"), booking(11, "15:00:00"))
        givenProfessionalFound()
        coEvery { servicesDataSource.fetchServicesByIds(listOf(1, 2)) } returns Response.success(services)
        coEvery {
            appointmentsDataSource.fetchProfessionalAppointments(PROFESSIONAL_ID, START_DATE, END_DATE)
        } returns Response.success(bookings)

        val emissions = fetch().toList()

        assertEquals(
            listOf(ApiState.Success(ProfessionalCalendarResponse(professional(PROFESSIONAL_ID), services, bookings))),
            emissions
        )
    }

    @Test
    fun `skips services call when no service is selected`() = runTest {
        givenProfessionalFound()
        coEvery { appointmentsDataSource.fetchProfessionalAppointments(any(), any(), any()) } returns
            Response.success(emptyList())

        val result = fetch(serviceIds = emptyList()).toList().single()

        assertEquals(
            ApiState.Success(ProfessionalCalendarResponse(professional(PROFESSIONAL_ID), emptyList(), emptyList())),
            result
        )
        coVerify(exactly = 0) { servicesDataSource.fetchServicesByIds(any()) }
    }

    @Test
    fun `emits error and stops when professional api fails`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalById(PROFESSIONAL_ID) } returns errorResponse(500)

        val result = fetch().toList().single()

        assertEquals(500, (result as ApiState.Error).code)
        coVerify(exactly = 0) { servicesDataSource.fetchServicesByIds(any()) }
        coVerify(exactly = 0) { appointmentsDataSource.fetchProfessionalAppointments(any(), any(), any()) }
    }

    @Test
    fun `emits not found error when professional does not exist`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalById(PROFESSIONAL_ID) } returns Response.success(emptyList())

        val result = fetch().toList().single()

        assertEquals(ApiState.Error("Professional $PROFESSIONAL_ID not found"), result)
        coVerify(exactly = 0) { servicesDataSource.fetchServicesByIds(any()) }
    }

    @Test
    fun `emits error and stops when services api fails`() = runTest {
        givenProfessionalFound()
        coEvery { servicesDataSource.fetchServicesByIds(any()) } returns errorResponse(404)

        val result = fetch().toList().single()

        assertEquals(ApiState.Error("Not found", 404), result)
        coVerify(exactly = 0) { appointmentsDataSource.fetchProfessionalAppointments(any(), any(), any()) }
    }

    @Test
    fun `emits error when appointments api fails`() = runTest {
        givenProfessionalFound()
        coEvery { servicesDataSource.fetchServicesByIds(any()) } returns Response.success(listOf(service(1)))
        coEvery { appointmentsDataSource.fetchProfessionalAppointments(any(), any(), any()) } returns errorResponse(500)

        val result = fetch().toList().single()

        assertEquals(500, (result as ApiState.Error).code)
    }

    @Test
    fun `emits error when network fails`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalById(PROFESSIONAL_ID) } throws IOException("no network")

        val result = fetch().toList().single()

        assertEquals(ApiState.Error("no network", -1), result)
    }

    private companion object {
        const val PROFESSIONAL_ID = 1L
        const val START_DATE = "2026-10-27"
        const val END_DATE = "2026-11-02"
    }
}
