package com.glowbook.android.data.remote

import com.glowbook.android.testutils.FakeApiInterceptor
import com.glowbook.android.testutils.createApiService
import com.glowbook.android.testutils.relativePath
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class AppointmentsHistoryApiServiceTest {

    private val interceptor = FakeApiInterceptor()
    private val apiService = createApiService(AppointmentsHistoryApiService::class.java, interceptor)

    private val historyJson = """
        [{"id":8,"id_servicos":3,"data":"2026-10-20","horario":"10:00:00","status":"concluido","ja_passou":true,
          "profissional_id":2,"profissional":"Bea","avaliacao":4.7,"categoria":"Unhas"}]
    """.trimIndent()

    @Test
    fun `appointmentsHistory sends GET to historico_agendamentos without status filter`() = runTest {
        apiService.appointmentsHistory()

        val request = interceptor.lastRequest
        assertEquals("GET", request.method)
        assertEquals("historico_agendamentos", request.relativePath)
        assertEquals("*", request.url.queryParameter("select"))
        assertNull(request.url.queryParameter("status"))
    }

    @Test
    fun `appointmentsHistory parses response body`() = runTest {
        interceptor.responseBody = historyJson

        val body = apiService.appointmentsHistory().body()!!

        assertEquals(1, body.size)
        assertEquals(8L, body.first().id)
        assertEquals(3L, body.first().idServicos)
    }

    @Test
    fun `appointmentsCancelled filters historico_agendamentos by cancelled status`() = runTest {
        apiService.appointmentsCancelled()

        val request = interceptor.lastRequest
        assertEquals("GET", request.method)
        assertEquals("historico_agendamentos", request.relativePath)
        assertEquals("*", request.url.queryParameter("select"))
        assertEquals("eq.cancelado", request.url.queryParameter("status"))
    }

    @Test
    fun `appointmentsCancelled exposes http error`() = runTest {
        interceptor.code = 500

        val response = apiService.appointmentsCancelled()

        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
    }
}
