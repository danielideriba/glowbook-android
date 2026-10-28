package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.AppointmentsUpNextDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.remote.model.AppointmentUpNextWithServices
import com.glowbook.android.domain.repository.AppointmentsUpNextRepository
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
import java.net.SocketTimeoutException

class AppointmentsUpNextRepositoryImplTest {

    private val upNextDataSource: AppointmentsUpNextDataSource = mockk()
    private val servicesDataSource: ServicesDataSource = mockk()
    private val repository: AppointmentsUpNextRepository =
        AppointmentsUpNextRepositoryImpl(upNextDataSource, servicesDataSource, TestDispatchersProvider())

    @Test
    fun `groups rows by appointment and attaches their services`() = runTest {
        coEvery { upNextDataSource.fetchAppointmentsUpNext() } returns Response.success(
            listOf(upNextRow(id = 1, idServicos = 10), upNextRow(id = 1, idServicos = 11), upNextRow(id = 2, idServicos = 11))
        )
        coEvery { servicesDataSource.fetchServicesByIds(listOf(10, 11)) } returns
            Response.success(listOf(service(10), service(11)))

        val emissions = repository.fetchAppointmentsUpNext().toList()

        assertEquals(
            listOf(
                ApiState.Success(
                    listOf(
                        AppointmentUpNextWithServices(upNextRow(1, 10), listOf(service(10), service(11))),
                        AppointmentUpNextWithServices(upNextRow(2, 11), listOf(service(11)))
                    )
                )
            ),
            emissions
        )
    }

    @Test
    fun `ignores service ids not returned by services api`() = runTest {
        coEvery { upNextDataSource.fetchAppointmentsUpNext() } returns
            Response.success(listOf(upNextRow(id = 1, idServicos = 99)))
        coEvery { servicesDataSource.fetchServicesByIds(listOf(99)) } returns Response.success(emptyList())

        val result = repository.fetchAppointmentsUpNext().toList().single()

        assertEquals(ApiState.Success(listOf(AppointmentUpNextWithServices(upNextRow(1, 99), emptyList()))), result)
    }

    @Test
    fun `emits empty list and skips services call when there are no appointments`() = runTest {
        coEvery { upNextDataSource.fetchAppointmentsUpNext() } returns Response.success(emptyList())

        val result = repository.fetchAppointmentsUpNext().toList().single()

        assertEquals(ApiState.Success(emptyList<AppointmentUpNextWithServices>()), result)
        coVerify(exactly = 0) { servicesDataSource.fetchServicesByIds(any()) }
    }

    @Test
    fun `emits error and stops when up next api fails`() = runTest {
        coEvery { upNextDataSource.fetchAppointmentsUpNext() } returns errorResponse(500)

        val result = repository.fetchAppointmentsUpNext().toList().single()

        assertEquals(500, (result as ApiState.Error).code)
        coVerify(exactly = 0) { servicesDataSource.fetchServicesByIds(any()) }
    }

    @Test
    fun `emits error when services api fails`() = runTest {
        coEvery { upNextDataSource.fetchAppointmentsUpNext() } returns
            Response.success(listOf(upNextRow(id = 1, idServicos = 10)))
        coEvery { servicesDataSource.fetchServicesByIds(any()) } returns errorResponse(404)

        val result = repository.fetchAppointmentsUpNext().toList().single()

        assertEquals(ApiState.Error("Not found", 404), result)
    }

    @Test
    fun `emits timeout error when server does not answer`() = runTest {
        coEvery { upNextDataSource.fetchAppointmentsUpNext() } throws SocketTimeoutException("timeout")

        val result = repository.fetchAppointmentsUpNext().toList().single()

        assertEquals(ApiState.Error("timeout", -1), result)
    }
}
