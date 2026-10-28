package com.glowbook.android.data.remote

import com.glowbook.android.testutils.FakeApiInterceptor
import com.glowbook.android.testutils.createApiService
import com.glowbook.android.testutils.relativePath
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfessionalsApiServiceTest {

    private val interceptor = FakeApiInterceptor()
    private val apiService = createApiService(ProfessionalsApiService::class.java, interceptor)

    @Test
    fun `profissionais sends GET to profissionais with available filter and limit`() = runTest {
        apiService.profissionais(limit = 30)

        val request = interceptor.lastRequest
        assertEquals("GET", request.method)
        assertEquals("profissionais", request.relativePath)
        assertEquals("*", request.url.queryParameter("select"))
        assertEquals("eq.true", request.url.queryParameter("disponivel"))
        assertEquals("id.asc", request.url.queryParameter("order"))
        assertEquals("30", request.url.queryParameter("limit"))
    }

    @Test
    fun `profissionais parses response body`() = runTest {
        interceptor.responseBody = """
            [{"id":1,"nome":"Camila Rocha","especialidade":"Cabeleireira","avaliacao":4.9,
              "cor_fundo":"#FDE8EC","disponivel":true,"categoria_id":1}]
        """.trimIndent()

        val professional = apiService.profissionais(limit = 1).body()!!.single()

        assertEquals("Camila Rocha", professional.nome)
        assertEquals("#FDE8EC", professional.corFundo)
        assertTrue(professional.disponivel)
    }

    @Test
    fun `profissionalById filters profissionais by id`() = runTest {
        apiService.profissionalById(id = "eq.7")

        val request = interceptor.lastRequest
        assertEquals("GET", request.method)
        assertEquals("profissionais", request.relativePath)
        assertEquals("*", request.url.queryParameter("select"))
        assertEquals("eq.7", request.url.queryParameter("id"))
        assertNull(request.url.queryParameter("disponivel"))
        assertNull(request.url.queryParameter("limit"))
    }

    @Test
    fun `profissionalById returns empty list when not found`() = runTest {
        val response = apiService.profissionalById(id = "eq.99")

        assertTrue(response.isSuccessful)
        assertTrue(response.body()!!.isEmpty())
    }
}
