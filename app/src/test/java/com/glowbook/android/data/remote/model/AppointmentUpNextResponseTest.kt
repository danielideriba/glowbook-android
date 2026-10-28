package com.glowbook.android.data.remote.model

import com.glowbook.android.di.NetworkModule
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class AppointmentUpNextResponseTest {

    private val gson = NetworkModule.provideGson()

    @Test
    fun `deserializes supabase json into all fields`() {
        val json = """
            {
              "id": 5,
              "id_servicos": 2,
              "data": "2026-10-30",
              "horario": "14:00:00",
              "status": "confirmado",
              "profissional_id": 1,
              "profissional": "Camila Rocha",
              "avatar": "👩",
              "avaliacao": 4.9,
              "categoria": "Cabelo"
            }
        """.trimIndent()

        val appointment = gson.fromJson(json, AppointmentUpNextResponse::class.java)

        assertEquals(5L, appointment.id)
        assertEquals(2L, appointment.idServicos)
        assertEquals("2026-10-30", appointment.data)
        assertEquals("14:00:00", appointment.horario)
        assertEquals("confirmado", appointment.status)
        assertEquals(1L, appointment.profissionalId)
        assertEquals("Camila Rocha", appointment.profissional)
        assertEquals("👩", appointment.avatar)
        assertEquals(4.9, appointment.avaliacao, 0.0)
        assertEquals("Cabelo", appointment.categoria)
    }

    @Test
    fun `deserializes missing avatar as null`() {
        val json = """{"id":5,"id_servicos":2,"data":"2026-10-30","horario":"14:00:00","status":"confirmado",
            "profissional_id":1,"profissional":"Camila","avaliacao":4.9,"categoria":"Cabelo"}"""

        val appointment = gson.fromJson(json, AppointmentUpNextResponse::class.java)

        assertNull(appointment.avatar)
    }

    @Test
    fun `constructor applies default values`() {
        val appointment = AppointmentUpNextResponse(
            id = 5,
            idServicos = 2,
            data = "2026-10-30",
            horario = "14:00:00",
            status = "confirmado",
            profissionalId = 1,
            profissional = "Camila",
            avaliacao = 4.9,
            categoria = "Cabelo"
        )

        assertNull(appointment.avatar)
    }

    @Test
    fun `serializes using snake case field names`() {
        val appointment = AppointmentUpNextResponse(5, 2, "2026-10-30", "14:00:00", "confirmado", 1, "Camila", null, 4.9, "Cabelo")

        val json = JsonParser.parseString(gson.toJson(appointment)).asJsonObject

        assertEquals(2L, json["id_servicos"].asLong)
        assertEquals(1L, json["profissional_id"].asLong)
        assertFalse(json.has("idServicos"))
        assertFalse(json.has("profissionalId"))
    }
}
