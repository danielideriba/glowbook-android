package com.glowbook.android.data.remote.model

import com.glowbook.android.di.NetworkModule
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppointmentsHistoryResponseTest {

    private val gson = NetworkModule.provideGson()

    @Test
    fun `deserializes supabase json with embedded service`() {
        val json = """
            {
              "id": 8,
              "id_servicos": 3,
              "data": "2026-10-20",
              "horario": "10:00:00",
              "status": "concluido",
              "ja_passou": true,
              "profissional_id": 2,
              "profissional": "Bea Lima",
              "avatar": "🧑",
              "avaliacao": 4.7,
              "categoria": "Unhas",
              "servicos": {"nome": "Nail Art", "icone": "nail-art-complete", "duracao_minutos": 60}
            }
        """.trimIndent()

        val appointment = gson.fromJson(json, AppointmentsHistoryResponse::class.java)

        assertEquals(8L, appointment.id)
        assertEquals(3L, appointment.idServicos)
        assertEquals("2026-10-20", appointment.data)
        assertEquals("10:00:00", appointment.horario)
        assertEquals("concluido", appointment.status)
        assertTrue(appointment.jaPassou)
        assertEquals(2L, appointment.profissionalId)
        assertEquals("Bea Lima", appointment.profissional)
        assertEquals("🧑", appointment.avatar)
        assertEquals(4.7, appointment.avaliacao, 0.0)
        assertEquals("Unhas", appointment.categoria)
        assertEquals(ServiceSummary("Nail Art", "nail-art-complete", 60), appointment.servico)
    }

    @Test
    fun `deserializes missing optional fields as null`() {
        val json = """{"id":8,"data":"2026-10-20","horario":"10:00:00","status":"cancelado","ja_passou":false,
            "profissional_id":2,"profissional":"Bea","avaliacao":4.7,"categoria":"Unhas"}"""

        val appointment = gson.fromJson(json, AppointmentsHistoryResponse::class.java)

        assertNull(appointment.idServicos)
        assertNull(appointment.avatar)
        assertNull(appointment.servico)
        assertFalse(appointment.jaPassou)
    }

    @Test
    fun `constructor applies default values`() {
        val appointment = AppointmentsHistoryResponse(
            id = 8,
            data = "2026-10-20",
            horario = "10:00:00",
            status = "concluido",
            jaPassou = true,
            profissionalId = 2,
            profissional = "Bea",
            avaliacao = 4.7,
            categoria = "Unhas"
        )

        assertNull(appointment.idServicos)
        assertNull(appointment.avatar)
        assertNull(appointment.servico)
    }

    @Test
    fun `serializes using snake case field names`() {
        val appointment = AppointmentsHistoryResponse(
            8, 3, "2026-10-20", "10:00:00", "concluido", true, 2, "Bea", null, 4.7, "Unhas",
            ServiceSummary("Nail Art")
        )

        val json = JsonParser.parseString(gson.toJson(appointment)).asJsonObject

        assertEquals(3L, json["id_servicos"].asLong)
        assertTrue(json["ja_passou"].asBoolean)
        assertEquals(2L, json["profissional_id"].asLong)
        assertEquals("Nail Art", json["servicos"].asJsonObject["nome"].asString)
        assertFalse(json.has("servico"))
        assertFalse(json.has("jaPassou"))
    }
}
