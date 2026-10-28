package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.AppointmentsResponse
import com.glowbook.android.testutils.FakeApiInterceptor
import com.glowbook.android.testutils.createApiService
import com.glowbook.android.testutils.relativePath
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppointmentsApiServiceTest {

    private val interceptor = FakeApiInterceptor()
    private val apiService = createApiService(AppointmentsApiService::class.java, interceptor)

    @Test
    fun `appointments sends GET to agendamentos with default query`() = runTest {
        apiService.appointments()

        val request = interceptor.lastRequest
        assertEquals("GET", request.method)
        assertEquals("agendamentos", request.relativePath)
        assertEquals("*", request.url.queryParameter("select"))
        assertEquals("neq.cancelado", request.url.queryParameter("status"))
        assertEquals("data.asc,horario.asc", request.url.queryParameter("order"))
        assertNull(request.url.queryParameter("profissional_id"))
        assertTrue(request.url.queryParameterValues("data").isEmpty())
    }

    @Test
    fun `appointments repeats data query for date range filter`() = runTest {
        apiService.appointments(profissionalId = "eq.1", data = listOf("gte.2026-10-27", "lte.2026-11-02"))

        val url = interceptor.lastRequest.url
        assertEquals("eq.1", url.queryParameter("profissional_id"))
        assertEquals(listOf("gte.2026-10-27", "lte.2026-11-02"), url.queryParameterValues("data"))
    }

    @Test
    fun `appointments omits status when null`() = runTest {
        apiService.appointments(status = null)

        assertNull(interceptor.lastRequest.url.queryParameter("status"))
    }

    @Test
    fun `appointments parses response body`() = runTest {
        interceptor.responseBody =
            """[{"id":10,"data":"2026-10-30","horario":"11:00:00","status":"confirmado","profissional_id":1}]"""

        val response = apiService.appointments()

        assertTrue(response.isSuccessful)
        assertEquals(listOf(AppointmentsResponse(10, "2026-10-30", "11:00:00", "confirmado", 1)), response.body())
    }

    @Test
    fun `appointments exposes http error`() = runTest {
        interceptor.code = 404
        interceptor.responseBody = """{"code":"PGRST205"}"""

        val response = apiService.appointments()

        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }
}
