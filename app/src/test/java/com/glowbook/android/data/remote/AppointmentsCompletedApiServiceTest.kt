package com.glowbook.android.data.remote

import com.glowbook.android.testutils.FakeApiInterceptor
import com.glowbook.android.testutils.createApiService
import com.glowbook.android.testutils.relativePath
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AppointmentsCompletedApiServiceTest {

    private val interceptor = FakeApiInterceptor()
    private val apiService = createApiService(AppointmentsCompletedApiService::class.java, interceptor)

    @Test
    fun `appointmentsCompleted sends GET to agendamentos_completos with defaults and limit`() = runTest {
        apiService.appointmentsCompleted(limit = 3)

        val request = interceptor.lastRequest
        assertEquals("GET", request.method)
        assertEquals("agendamentos_completos", request.relativePath)
        assertEquals("*", request.url.queryParameter("select"))
        assertEquals("in.(confirmado,pendente)", request.url.queryParameter("status"))
        assertEquals("data.asc,horario.asc", request.url.queryParameter("order"))
        assertEquals("3", request.url.queryParameter("limit"))
    }

    @Test
    fun `appointmentsCompleted parses response body`() = runTest {
        interceptor.responseBody = """
            [{"id":12,"servicos":"Corte + Escova","valor":260.0,"duracao_total":135,"data":"2026-10-15",
              "horario":"09:00:00","status":"confirmado","profissional_id":1,"profissional":"Camila",
              "avaliacao":4.9,"categoria":"Cabelo"}]
        """.trimIndent()

        val appointment = apiService.appointmentsCompleted(limit = 1).body()!!.single()

        assertEquals(12L, appointment.id)
        assertEquals(135, appointment.duracaoTotal)
    }
}
