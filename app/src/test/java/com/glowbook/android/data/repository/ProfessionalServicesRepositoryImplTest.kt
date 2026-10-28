package com.glowbook.android.data.repository

import com.glowbook.android.data.datasource.ProfessionalsDataSource
import com.glowbook.android.data.datasource.ServicesDataSource
import com.glowbook.android.data.remote.model.ProfessionalWithServices
import com.glowbook.android.domain.repository.ProfessionalServicesRepository
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

class ProfessionalServicesRepositoryImplTest {

    private val professionalsDataSource: ProfessionalsDataSource = mockk()
    private val servicesDataSource: ServicesDataSource = mockk()
    private val repository: ProfessionalServicesRepository =
        ProfessionalServicesRepositoryImpl(professionalsDataSource, servicesDataSource, TestDispatchersProvider())

    @Test
    fun `emits professional with all services`() = runTest {
        val services = listOf(service(1), service(2), service(3))
        coEvery { professionalsDataSource.fetchProfessionalById(1) } returns Response.success(listOf(professional(1)))
        coEvery { servicesDataSource.fetchAllServices() } returns Response.success(services)

        val emissions = repository.fetchProfessionalServices(1).toList()

        assertEquals(listOf(ApiState.Success(ProfessionalWithServices(professional(1), services))), emissions)
    }

    @Test
    fun `emits professional with empty services list`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalById(1) } returns Response.success(listOf(professional(1)))
        coEvery { servicesDataSource.fetchAllServices() } returns Response.success(emptyList())

        val result = repository.fetchProfessionalServices(1).toList().single()

        assertEquals(ApiState.Success(ProfessionalWithServices(professional(1), emptyList())), result)
    }

    @Test
    fun `emits not found error when professional does not exist`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalById(99) } returns Response.success(emptyList())

        val result = repository.fetchProfessionalServices(99).toList().single()

        assertEquals(ApiState.Error("Professional 99 not found"), result)
        coVerify(exactly = 0) { servicesDataSource.fetchAllServices() }
    }

    @Test
    fun `emits error and stops when professional api fails`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalById(1) } returns errorResponse(500)

        val result = repository.fetchProfessionalServices(1).toList().single()

        assertEquals(500, (result as ApiState.Error).code)
        coVerify(exactly = 0) { servicesDataSource.fetchAllServices() }
    }

    @Test
    fun `emits error when services api fails`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalById(1) } returns Response.success(listOf(professional(1)))
        coEvery { servicesDataSource.fetchAllServices() } returns errorResponse(404)

        val result = repository.fetchProfessionalServices(1).toList().single()

        assertEquals(ApiState.Error("Not found", 404), result)
    }

    @Test
    fun `emits error when network fails`() = runTest {
        coEvery { professionalsDataSource.fetchProfessionalById(1) } throws IOException("no network")

        val result = repository.fetchProfessionalServices(1).toList().single()

        assertEquals(ApiState.Error("no network", -1), result)
    }
}
