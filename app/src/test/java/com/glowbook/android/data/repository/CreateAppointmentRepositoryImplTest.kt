package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.CreateAppointmentDataSource
import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import com.glowbook.android.domain.repository.CreateAppointmentRepository
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

class CreateAppointmentRepositoryImplTest {

    private val createAppointmentDataSource: CreateAppointmentDataSource = mockk()
    private val repository: CreateAppointmentRepository =
        CreateAppointmentRepositoryImpl(createAppointmentDataSource, TestDispatchersProvider())

    private val request = CreateAppointmentRequest(
        data = "2026-10-30",
        horario = "14:00",
        profissionalId = 1,
        servicos = listOf(1, 2)
    )

    @Test
    fun `emits created appointment id on success`() = runTest {
        coEvery { createAppointmentDataSource.createAppointment(request) } returns Response.success(42L)

        val emissions = repository.createAppointment(request).toList()

        assertEquals(listOf(ApiState.Success(42L)), emissions)
    }

    @Test
    fun `sends request to data source once`() = runTest {
        coEvery { createAppointmentDataSource.createAppointment(any()) } returns Response.success(42L)

        repository.createAppointment(request).toList()

        coVerify(exactly = 1) { createAppointmentDataSource.createAppointment(request) }
    }

    @Test
    fun `emits error when rpc rejects the request`() = runTest {
        coEvery { createAppointmentDataSource.createAppointment(request) } returns errorResponse(400)

        val result = repository.createAppointment(request).toList().single()

        assertEquals(400, (result as ApiState.Error).code)
    }

    @Test
    fun `emits error when rpc returns empty body`() = runTest {
        coEvery { createAppointmentDataSource.createAppointment(request) } returns Response.success(null)

        val result = repository.createAppointment(request).toList().single()

        assertEquals(ApiState.Error("Empty body"), result)
    }

    @Test
    fun `emits error when network fails`() = runTest {
        coEvery { createAppointmentDataSource.createAppointment(request) } throws IOException("no network")

        val result = repository.createAppointment(request).toList().single()

        assertEquals(ApiState.Error("no network", -1), result)
    }
}
