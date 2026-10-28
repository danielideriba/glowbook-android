package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.AppointmentsCancelledDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.remote.model.AppointmentHistoryWithServices
import com.glowbook.android.domain.repository.AppointmentsCancelledRepository
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

class AppointmentsCancelledRepositoryImplTest {

    private val cancelledDataSource: AppointmentsCancelledDataSource = mockk()
    private val servicesDataSource: ServicesDataSource = mockk()
    private val repository: AppointmentsCancelledRepository =
        AppointmentsCancelledRepositoryImpl(cancelledDataSource, servicesDataSource, TestDispatchersProvider())

    @Test
    fun `groups rows by appointment and attaches their services`() = runTest {
        coEvery { cancelledDataSource.fetchAppointmentsCancelled() } returns Response.success(
            listOf(
                historyRow(id = 1, idServicos = 10, status = "cancelado"),
                historyRow(id = 1, idServicos = 11, status = "cancelado"),
                historyRow(id = 2, idServicos = 10, status = "cancelado")
            )
        )
        coEvery { servicesDataSource.fetchServicesByIds(listOf(10, 11)) } returns
            Response.success(listOf(service(10), service(11)))

        val emissions = repository.fetchAppointmentsCancelled().toList()

        assertEquals(
            listOf(
                ApiState.Success(
                    listOf(
                        AppointmentHistoryWithServices(historyRow(1, 10, "cancelado"), listOf(service(10), service(11))),
                        AppointmentHistoryWithServices(historyRow(2, 10, "cancelado"), listOf(service(10)))
                    )
                )
            ),
            emissions
        )
    }

    @Test
    fun `skips services call when rows have no service ids`() = runTest {
        coEvery { cancelledDataSource.fetchAppointmentsCancelled() } returns
            Response.success(listOf(historyRow(id = 1, idServicos = null, status = "cancelado")))

        val result = repository.fetchAppointmentsCancelled().toList().single()

        assertEquals(
            ApiState.Success(listOf(AppointmentHistoryWithServices(historyRow(1, null, "cancelado"), emptyList()))),
            result
        )
        coVerify(exactly = 0) { servicesDataSource.fetchServicesByIds(any()) }
    }

    @Test
    fun `ignores service ids not returned by services api`() = runTest {
        coEvery { cancelledDataSource.fetchAppointmentsCancelled() } returns
            Response.success(listOf(historyRow(id = 1, idServicos = 99, status = "cancelado")))
        coEvery { servicesDataSource.fetchServicesByIds(listOf(99)) } returns Response.success(emptyList())

        val result = repository.fetchAppointmentsCancelled().toList().single() as ApiState.Success

        assertEquals(emptyList<Any>(), result.data.single().services)
    }

    @Test
    fun `emits empty list when there are no cancelled appointments`() = runTest {
        coEvery { cancelledDataSource.fetchAppointmentsCancelled() } returns Response.success(emptyList())

        val result = repository.fetchAppointmentsCancelled().toList().single()

        assertEquals(ApiState.Success(emptyList<AppointmentHistoryWithServices>()), result)
    }

    @Test
    fun `emits error and stops when cancelled api fails`() = runTest {
        coEvery { cancelledDataSource.fetchAppointmentsCancelled() } returns errorResponse(500)

        val result = repository.fetchAppointmentsCancelled().toList().single()

        assertEquals(500, (result as ApiState.Error).code)
        coVerify(exactly = 0) { servicesDataSource.fetchServicesByIds(any()) }
    }

    @Test
    fun `emits error when services api fails`() = runTest {
        coEvery { cancelledDataSource.fetchAppointmentsCancelled() } returns
            Response.success(listOf(historyRow(id = 1, idServicos = 10, status = "cancelado")))
        coEvery { servicesDataSource.fetchServicesByIds(any()) } returns errorResponse(404)

        val result = repository.fetchAppointmentsCancelled().toList().single()

        assertEquals(ApiState.Error("Not found", 404), result)
    }

    @Test
    fun `emits error when network fails`() = runTest {
        coEvery { cancelledDataSource.fetchAppointmentsCancelled() } throws IOException("no network")

        val result = repository.fetchAppointmentsCancelled().toList().single()

        assertEquals(ApiState.Error("no network", -1), result)
    }
}
