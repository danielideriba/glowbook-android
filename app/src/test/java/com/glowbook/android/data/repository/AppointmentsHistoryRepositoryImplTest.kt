package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.AppointmentsHistoryDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.remote.model.AppointmentHistoryWithServices
import com.glowbook.android.domain.repository.AppointmentsHistoryRepository
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
import java.net.UnknownHostException

class AppointmentsHistoryRepositoryImplTest {

    private val historyDataSource: AppointmentsHistoryDataSource = mockk()
    private val servicesDataSource: ServicesDataSource = mockk()
    private val repository: AppointmentsHistoryRepository =
        AppointmentsHistoryRepositoryImpl(historyDataSource, servicesDataSource, TestDispatchersProvider())

    @Test
    fun `groups rows by appointment and attaches their services`() = runTest {
        coEvery { historyDataSource.fetchAppointmentsHistory() } returns Response.success(
            listOf(historyRow(id = 1, idServicos = 10), historyRow(id = 1, idServicos = 11), historyRow(id = 2, idServicos = null))
        )
        coEvery { servicesDataSource.fetchServicesByIds(listOf(10, 11)) } returns
            Response.success(listOf(service(10), service(11)))

        val emissions = repository.fetchAppointmentsHistory().toList()

        assertEquals(
            listOf(
                ApiState.Success(
                    listOf(
                        AppointmentHistoryWithServices(historyRow(1, 10), listOf(service(10), service(11))),
                        AppointmentHistoryWithServices(historyRow(2, null), emptyList())
                    )
                )
            ),
            emissions
        )
    }

    @Test
    fun `requests each service id only once`() = runTest {
        coEvery { historyDataSource.fetchAppointmentsHistory() } returns Response.success(
            listOf(historyRow(id = 1, idServicos = 10), historyRow(id = 2, idServicos = 10))
        )
        coEvery { servicesDataSource.fetchServicesByIds(any()) } returns Response.success(listOf(service(10)))

        repository.fetchAppointmentsHistory().toList()

        coVerify(exactly = 1) { servicesDataSource.fetchServicesByIds(listOf(10)) }
    }

    @Test
    fun `skips services call when rows have no service ids`() = runTest {
        coEvery { historyDataSource.fetchAppointmentsHistory() } returns
            Response.success(listOf(historyRow(id = 1, idServicos = null)))

        val result = repository.fetchAppointmentsHistory().toList().single()

        assertEquals(ApiState.Success(listOf(AppointmentHistoryWithServices(historyRow(1, null), emptyList()))), result)
        coVerify(exactly = 0) { servicesDataSource.fetchServicesByIds(any()) }
    }

    @Test
    fun `emits error and stops when history api fails`() = runTest {
        coEvery { historyDataSource.fetchAppointmentsHistory() } returns errorResponse(404)

        val result = repository.fetchAppointmentsHistory().toList().single()

        assertEquals(ApiState.Error("Not found", 404), result)
        coVerify(exactly = 0) { servicesDataSource.fetchServicesByIds(any()) }
    }

    @Test
    fun `emits error when history body is empty`() = runTest {
        coEvery { historyDataSource.fetchAppointmentsHistory() } returns Response.success(null)

        val result = repository.fetchAppointmentsHistory().toList().single()

        assertEquals(ApiState.Error("Empty body"), result)
    }

    @Test
    fun `emits error when services api fails`() = runTest {
        coEvery { historyDataSource.fetchAppointmentsHistory() } returns
            Response.success(listOf(historyRow(id = 1, idServicos = 10)))
        coEvery { servicesDataSource.fetchServicesByIds(any()) } returns errorResponse(500)

        val result = repository.fetchAppointmentsHistory().toList().single()

        assertEquals(500, (result as ApiState.Error).code)
    }

    @Test
    fun `emits offline error when host is unreachable`() = runTest {
        coEvery { historyDataSource.fetchAppointmentsHistory() } throws UnknownHostException()

        val result = repository.fetchAppointmentsHistory().toList().single()

        assertEquals(ApiState.Error("Sem conexão com a internet", -1), result)
    }
}
