package com.glowbook.android.data.remote

import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import com.glowbook.android.testutils.FakeApiInterceptor
import com.glowbook.android.testutils.bodyAsString
import com.glowbook.android.testutils.createApiService
import com.glowbook.android.testutils.relativePath
import com.google.gson.JsonParser
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateAppointmentApiServiceTest {

    private val interceptor = FakeApiInterceptor(responseBody = "42")
    private val apiService = createApiService(CreateAppointmentApiService::class.java, interceptor)

    private val request = CreateAppointmentRequest(
        data = "2026-10-30",
        horario = "14:00",
        profissionalId = 1,
        servicos = listOf(1, 2)
    )

    @Test
    fun `createAppointment sends POST to criar_agendamento rpc`() = runTest {
        apiService.createAppointment(request)

        val sent = interceptor.lastRequest
        assertEquals("POST", sent.method)
        assertEquals("rpc/criar_agendamento", sent.relativePath)
        assertEquals("application/json; charset=UTF-8", sent.body!!.contentType().toString())
    }

    @Test
    fun `createAppointment sends rpc params as json body`() = runTest {
        apiService.createAppointment(request)

        val body = JsonParser.parseString(interceptor.lastRequest.bodyAsString()).asJsonObject
        assertEquals("2026-10-30", body["p_data"].asString)
        assertEquals("14:00", body["p_horario"].asString)
        assertEquals(1L, body["p_profissional_id"].asLong)
        assertEquals(listOf(1L, 2L), body["p_servicos"].asJsonArray.map { it.asLong })
    }

    @Test
    fun `createAppointment parses created id`() = runTest {
        val response = apiService.createAppointment(request)

        assertTrue(response.isSuccessful)
        assertEquals(42L, response.body())
    }

    @Test
    fun `createAppointment exposes rpc error`() = runTest {
        interceptor.code = 400
        interceptor.responseBody = """{"code":"22007","message":"invalid input syntax for type date"}"""

        val response = apiService.createAppointment(request)

        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())
        assertNull(response.body())
    }
}
