package com.glowbook.android.data.remote.model

import com.glowbook.android.di.NetworkModule
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class AppointmentsServiceResponseTest {

    private val gson = NetworkModule.provideGson()

    @Test
    fun `deserializes supabase json with embedded service`() {
        val json = """
            {
              "agendamento_id": 5,
              "preco": 120.0,
              "servicos": {"nome": "Corte", "icone": "scissors", "duracao_minutos": 45}
            }
        """.trimIndent()

        val response = gson.fromJson(json, AppointmentsServiceResponse::class.java)

        assertEquals(5L, response.agendamentoId)
        assertEquals(120.0, response.preco, 0.0)
        assertEquals("Corte", response.servico.nome)
        assertEquals("scissors", response.servico.icone)
        assertEquals(45, response.servico.duracaoMinutos)
    }

    @Test
    fun `serializes using snake case field names`() {
        val response = AppointmentsServiceResponse(5, 120.0, ServiceSummary("Corte", "scissors", 45))

        val json = JsonParser.parseString(gson.toJson(response)).asJsonObject

        assertEquals(5L, json["agendamento_id"].asLong)
        assertEquals(45, json["servicos"].asJsonObject["duracao_minutos"].asInt)
        assertFalse(json.has("agendamentoId"))
        assertFalse(json.has("servico"))
    }

    @Test
    fun `service summary deserializes missing optional fields as null`() {
        val summary = gson.fromJson("""{"nome":"Corte"}""", ServiceSummary::class.java)

        assertEquals("Corte", summary.nome)
        assertNull(summary.icone)
        assertNull(summary.duracaoMinutos)
    }

    @Test
    fun `service summary constructor applies default values`() {
        val summary = ServiceSummary(nome = "Corte")

        assertNull(summary.icone)
        assertNull(summary.duracaoMinutos)
    }
}
