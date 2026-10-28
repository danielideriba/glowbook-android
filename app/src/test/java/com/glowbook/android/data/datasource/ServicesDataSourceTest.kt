package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.ServicesApiService
import com.glowbook.android.data.remote.model.ServicesResponse
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

class ServicesDataSourceTest {

    private val apiService: ServicesApiService = mockk()
    private lateinit var dataSource: ServicesDataSource

    @Before
    fun setUp() {
        dataSource = ServicesDataSourceImpl(apiService)
    }

    // fetchServicesByIds

    @Test
    fun `fetchServicesByIds returns selected services from api`() = runTest {
        val services = listOf(service(id = 1), service(id = 3))
        coEvery { apiService.services(id = "in.(1,3)") } returns Response.success(services)

        val response = dataSource.fetchServicesByIds(listOf(1, 3))

        assertTrue(response.isSuccessful)
        assertEquals(services, response.body())
    }

    @Test
    fun `fetchServicesByIds filters api with in operator`() = runTest {
        coEvery { apiService.services(any(), any(), any(), any()) } returns Response.success(emptyList())

        dataSource.fetchServicesByIds(listOf(1, 3, 5))

        coVerify(exactly = 1) {
            apiService.services(select = "*", ativo = "eq.true", id = "in.(1,3,5)", order = "id.asc")
        }
        confirmVerified(apiService)
    }

    @Test
    fun `fetchServicesByIds sends single id inside in operator`() = runTest {
        coEvery { apiService.services(any(), any(), any(), any()) } returns Response.success(emptyList())

        dataSource.fetchServicesByIds(listOf(2))

        coVerify(exactly = 1) { apiService.services(id = "in.(2)") }
    }

    @Test
    fun `fetchServicesByIds sends empty in operator when no ids are given`() = runTest {
        coEvery { apiService.services(any(), any(), any(), any()) } returns Response.success(emptyList())

        dataSource.fetchServicesByIds(emptyList())

        coVerify(exactly = 1) { apiService.services(id = "in.()") }
    }

    @Test
    fun `fetchServicesByIds returns error response when api fails`() = runTest {
        coEvery { apiService.services(id = "in.(1)") } returns Response.error(400, errorBody())

        val response = dataSource.fetchServicesByIds(listOf(1))

        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())
        assertNull(response.body())
    }

    @Test(expected = IOException::class)
    fun `fetchServicesByIds propagates network exceptions`() = runTest {
        coEvery { apiService.services(id = "in.(1)") } throws IOException("no network")

        dataSource.fetchServicesByIds(listOf(1))
    }

    // fetchAllServices

    @Test
    fun `fetchAllServices returns all services from api`() = runTest {
        val services = listOf(service(id = 1), service(id = 2), service(id = 3))
        coEvery { apiService.services() } returns Response.success(services)

        val response = dataSource.fetchAllServices()

        assertTrue(response.isSuccessful)
        assertEquals(services, response.body())
    }

    @Test
    fun `fetchAllServices returns the exact api response`() = runTest {
        val apiResponse = Response.success(listOf(service(id = 1)))
        coEvery { apiService.services() } returns apiResponse

        val response = dataSource.fetchAllServices()

        assertSame(apiResponse, response)
    }

    @Test
    fun `fetchAllServices calls api without id filter`() = runTest {
        coEvery { apiService.services(any(), any(), any(), any()) } returns Response.success(emptyList())

        dataSource.fetchAllServices()

        coVerify(exactly = 1) {
            apiService.services(select = "*", ativo = "eq.true", id = null, order = "id.asc")
        }
        confirmVerified(apiService)
    }

    @Test
    fun `fetchAllServices returns empty list when there are no active services`() = runTest {
        coEvery { apiService.services() } returns Response.success(emptyList())

        val response = dataSource.fetchAllServices()

        assertTrue(response.isSuccessful)
        assertEquals(emptyList<ServicesResponse>(), response.body())
    }

    @Test
    fun `fetchAllServices returns error response when api fails`() = runTest {
        coEvery { apiService.services() } returns Response.error(500, errorBody())

        val response = dataSource.fetchAllServices()

        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
        assertNull(response.body())
    }

    @Test(expected = IOException::class)
    fun `fetchAllServices propagates network exceptions`() = runTest {
        coEvery { apiService.services() } throws IOException("no network")

        dataSource.fetchAllServices()
    }

    private fun errorBody() = """{"message":"bad request"}"""
        .toResponseBody("application/json".toMediaType())

    private fun service(id: Long) = ServicesResponse(
        id = id,
        nome = "Corte",
        categoriaId = 1,
        preco = 120.0,
        duracaoMinutos = 60,
        icone = "scissors"
    )
}
