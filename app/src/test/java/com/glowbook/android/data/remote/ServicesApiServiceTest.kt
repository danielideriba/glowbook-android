package com.glowbook.android.data.remote

import com.glowbook.android.testutils.FakeApiInterceptor
import com.glowbook.android.testutils.createApiService
import com.glowbook.android.testutils.relativePath
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ServicesApiServiceTest {

    private val interceptor = FakeApiInterceptor()
    private val apiService = createApiService(ServicesApiService::class.java, interceptor)

    @Test
    fun `services sends GET to servicos with active filter and no id`() = runTest {
        apiService.services()

        val request = interceptor.lastRequest
        assertEquals("GET", request.method)
        assertEquals("servicos", request.relativePath)
        assertEquals("*", request.url.queryParameter("select"))
        assertEquals("eq.true", request.url.queryParameter("ativo"))
        assertEquals("id.asc", request.url.queryParameter("order"))
        assertNull(request.url.queryParameter("id"))
    }

    @Test
    fun `services filters by ids`() = runTest {
        apiService.services(id = "in.(1,3)")

        assertEquals("in.(1,3)", interceptor.lastRequest.url.queryParameter("id"))
    }

    @Test
    fun `services parses response body`() = runTest {
        interceptor.responseBody = """
            [{"id":1,"nome":"Corte","categoria_id":1,"preco":120.0,"duracao_minutos":45,"ativo":true,"icone":"scissors"}]
        """.trimIndent()

        val service = apiService.services().body()!!.single()

        assertEquals("Corte", service.nome)
        assertEquals(45, service.duracaoMinutos)
        assertEquals("scissors", service.icone)
    }
}
