package com.glowbook.android.data.datasource

import com.glowbook.android.data.remote.ProfessionalsApiService
import com.glowbook.android.data.remote.model.ProfessionalsResponse
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

class ProfessionalsDataSourceTest {

    private val apiService: ProfessionalsApiService = mockk()
    private lateinit var dataSource: ProfessionalsDataSource

    @Before
    fun setUp() {
        dataSource = ProfessionalsDataSourceImpl(apiService)
    }

    // fetchProfessionalsAvailable

    @Test
    fun `fetchProfessionalsAvailable returns available professionals from api`() = runTest {
        val professionals = listOf(professional(id = 1), professional(id = 2))
        coEvery { apiService.profissionais(limit = 30) } returns Response.success(professionals)

        val response = dataSource.fetchProfessionalsAvailable(30)

        assertTrue(response.isSuccessful)
        assertEquals(professionals, response.body())
    }

    @Test
    fun `fetchProfessionalsAvailable returns the exact api response`() = runTest {
        val apiResponse = Response.success(listOf(professional(id = 1)))
        coEvery { apiService.profissionais(limit = 30) } returns apiResponse

        val response = dataSource.fetchProfessionalsAvailable(30)

        assertSame(apiResponse, response)
    }

    @Test
    fun `fetchProfessionalsAvailable passes limit and available filter to api`() = runTest {
        coEvery { apiService.profissionais(any(), any(), any(), any()) } returns Response.success(emptyList())

        dataSource.fetchProfessionalsAvailable(5)

        coVerify(exactly = 1) {
            apiService.profissionais(select = "*", disponivel = "eq.true", order = "id.asc", limit = 5)
        }
        confirmVerified(apiService)
    }

    @Test
    fun `fetchProfessionalsAvailable returns empty list when nobody is available`() = runTest {
        coEvery { apiService.profissionais(limit = 30) } returns Response.success(emptyList())

        val response = dataSource.fetchProfessionalsAvailable(30)

        assertTrue(response.isSuccessful)
        assertEquals(emptyList<ProfessionalsResponse>(), response.body())
    }

    @Test
    fun `fetchProfessionalsAvailable returns error response when api fails`() = runTest {
        coEvery { apiService.profissionais(limit = 30) } returns Response.error(500, errorBody())

        val response = dataSource.fetchProfessionalsAvailable(30)

        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
        assertNull(response.body())
    }

    @Test(expected = IOException::class)
    fun `fetchProfessionalsAvailable propagates network exceptions`() = runTest {
        coEvery { apiService.profissionais(limit = 30) } throws IOException("no network")

        dataSource.fetchProfessionalsAvailable(30)
    }

    // fetchProfessionalById

    @Test
    fun `fetchProfessionalById returns professional from api`() = runTest {
        val professionals = listOf(professional(id = 7))
        coEvery { apiService.profissionalById(id = "eq.7") } returns Response.success(professionals)

        val response = dataSource.fetchProfessionalById(7)

        assertTrue(response.isSuccessful)
        assertEquals(professionals, response.body())
    }

    @Test
    fun `fetchProfessionalById filters api by id with eq operator`() = runTest {
        coEvery { apiService.profissionalById(any(), any()) } returns Response.success(emptyList())

        dataSource.fetchProfessionalById(7)

        coVerify(exactly = 1) { apiService.profissionalById(select = "*", id = "eq.7") }
        confirmVerified(apiService)
    }

    @Test
    fun `fetchProfessionalById returns empty list when professional does not exist`() = runTest {
        coEvery { apiService.profissionalById(id = "eq.99") } returns Response.success(emptyList())

        val response = dataSource.fetchProfessionalById(99)

        assertTrue(response.isSuccessful)
        assertEquals(emptyList<ProfessionalsResponse>(), response.body())
    }

    @Test
    fun `fetchProfessionalById returns error response when api fails`() = runTest {
        coEvery { apiService.profissionalById(id = "eq.7") } returns Response.error(500, errorBody())

        val response = dataSource.fetchProfessionalById(7)

        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
        assertNull(response.body())
    }

    @Test(expected = IOException::class)
    fun `fetchProfessionalById propagates network exceptions`() = runTest {
        coEvery { apiService.profissionalById(id = "eq.7") } throws IOException("no network")

        dataSource.fetchProfessionalById(7)
    }

    private fun errorBody() = """{"message":"internal error"}"""
        .toResponseBody("application/json".toMediaType())

    private fun professional(id: Long) = ProfessionalsResponse(
        id = id,
        nome = "Camila Rocha",
        especialidade = "Cabeleireira",
        avaliacao = 4.9,
        categoriaId = 1
    )
}
