package com.glowbook.android.data.remote.model

import com.glowbook.android.di.NetworkModule
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class AppointmentsCompletedResponseTest {

    private val gson = NetworkModule.provideGson()

    @Test
    fun `deserializes supabase json into all fields`() {
        val json = """
            {
              "id": 12,
              "servicos": "Corte + Escova",
              "valor": 260.0,
              "duracao_total": 135,
              "data": "2026-10-15",
              "horario": "09:00:00",
              "status": "concluido",
              "profissional_id": 1,
              "profissional": "Camila Rocha",
              "avatar": "👩",
              "avaliacao": 4.9,
              "categoria": "Cabelo"
            }
        """.trimIndent()

        val appointment = gson.fromJson(json, AppointmentsCompletedResponse::class.java)

        assertEquals(12L, appointment.id)
        assertEquals("Corte + Escova", appointment.servicos)
        assertEquals(260.0, appointment.valor!!, 0.0)
        assertEquals(135, appointment.duracaoTotal)
        assertEquals("2026-10-15", appointment.data)
        assertEquals("09:00:00", appointment.horario)
        assertEquals("concluido", appointment.status)
        assertEquals(1L, appointment.profissionalId)
        assertEquals("Camila Rocha", appointment.profissional)
        assertEquals("👩", appointment.avatar)
        assertEquals(4.9, appointment.avaliacao, 0.0)
        assertEquals("Cabelo", appointment.categoria)
    }

    @Test
    fun `deserializes missing optional fields as null`() {
        val json = """{"id":12,"data":"2026-10-15","horario":"09:00:00","status":"concluido",
            "profissional_id":1,"profissional":"Camila","avaliacao":4.9,"categoria":"Cabelo"}"""

        val appointment = gson.fromJson(json, AppointmentsCompletedResponse::class.java)

        assertNull(appointment.servicos)
        assertNull(appointment.valor)
        assertNull(appointment.duracaoTotal)
        assertNull(appointment.avatar)
    }

    @Test
    fun `constructor applies default values`() {
        val appointment = AppointmentsCompletedResponse(
            id = 12,
            data = "2026-10-15",
            horario = "09:00:00",
            status = "concluido",
            profissionalId = 1,
            profissional = "Camila",
            avaliacao = 4.9,
            categoria = "Cabelo"
        )

        assertNull(appointment.servicos)
        assertNull(appointment.valor)
        assertNull(appointment.duracaoTotal)
        assertNull(appointment.avatar)
    }

    @Test
    fun `serializes using snake case field names`() {
        val appointment = AppointmentsCompletedResponse(
            12, "Corte", 120.0, 60, "2026-10-15", "09:00:00", "concluido", 1, "Camila", null, 4.9, "Cabelo"
        )

        val json = JsonParser.parseString(gson.toJson(appointment)).asJsonObject

        assertEquals(60, json["duracao_total"].asInt)
        assertEquals(1L, json["profissional_id"].asLong)
        assertFalse(json.has("duracaoTotal"))
        assertFalse(json.has("profissionalId"))
    }
}
