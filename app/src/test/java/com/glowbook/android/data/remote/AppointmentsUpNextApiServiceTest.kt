package com.glowbook.android.data.remote

import com.glowbook.android.testutils.FakeApiInterceptor
import com.glowbook.android.testutils.createApiService
import com.glowbook.android.testutils.relativePath
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppointmentsUpNextApiServiceTest {

    private val interceptor = FakeApiInterceptor()
    private val apiService = createApiService(AppointmentsUpNextApiService::class.java, interceptor)

    @Test
    fun `appointmentsUpNext sends GET to proximos_agendamentos ordered by date and time`() = runTest {
        apiService.appointmentsUpNext()

        val request = interceptor.lastRequest
        assertEquals("GET", request.method)
        assertEquals("proximos_agendamentos", request.relativePath)
        assertEquals("*", request.url.queryParameter("select"))
        assertEquals("data.asc,horario.asc", request.url.queryParameter("order"))
    }

    @Test
    fun `appointmentsUpNext parses response body`() = runTest {
        interceptor.responseBody = """
            [{"id":5,"id_servicos":2,"data":"2026-10-30","horario":"14:00:00","status":"confirmado",
              "profissional_id":1,"profissional":"Camila","avaliacao":4.9,"categoria":"Cabelo"}]
        """.trimIndent()

        val response = apiService.appointmentsUpNext()

        assertTrue(response.isSuccessful)
        assertEquals(5L, response.body()!!.single().id)
        assertEquals(2L, response.body()!!.single().idServicos)
    }
}
