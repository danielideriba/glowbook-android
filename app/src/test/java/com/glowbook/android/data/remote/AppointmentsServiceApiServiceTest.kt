package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.AppointmentsServiceResponse
import com.glowbook.android.data.remote.model.ServiceSummary
import com.glowbook.android.testutils.FakeApiInterceptor
import com.glowbook.android.testutils.createApiService
import com.glowbook.android.testutils.relativePath
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException

class AppointmentsServiceApiServiceTest {

    private val interceptor = FakeApiInterceptor()
    private val apiService = createApiService(AppointmentsServiceApiService::class.java, interceptor)

    @Test
    fun `appointmentsServices sends GET to agendamento_servicos with embedded service select`() = runTest {
        apiService.appointmentsServices()

        val request = interceptor.lastRequest
        assertEquals("GET", request.method)
        assertEquals("agendamento_servicos", request.relativePath)
        assertEquals(
            "agendamento_id,preco,servicos(nome,icone,duracao_minutos)",
            request.url.queryParameter("select")
        )
        assertEquals("agendamento_id.asc", request.url.queryParameter("order"))
        assertNull(request.url.queryParameter("agendamento_id"))
    }

    @Test
    fun `appointmentsServices filters by appointment id`() = runTest {
        apiService.appointmentsServices(agendamentoId = "eq.5")

        assertEquals("eq.5", interceptor.lastRequest.url.queryParameter("agendamento_id"))
    }

    @Test
    fun `appointmentsServices parses body directly`() = runTest {
        interceptor.responseBody =
            """[{"agendamento_id":5,"preco":120.0,"servicos":{"nome":"Corte","icone":"scissors","duracao_minutos":45}}]"""

        val services = apiService.appointmentsServices()

        assertEquals(listOf(AppointmentsServiceResponse(5, 120.0, ServiceSummary("Corte", "scissors", 45))), services)
    }

    @Test(expected = HttpException::class)
    fun `appointmentsServices throws on http error because it does not return Response`() = runTest {
        interceptor.code = 500

        apiService.appointmentsServices()
    }
}
