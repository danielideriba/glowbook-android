package com.glowbook.android.data.remote.model

import com.glowbook.android.di.NetworkModule
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class AppointmentsResponseTest {

    private val gson = NetworkModule.provideGson()

    @Test
    fun `deserializes supabase json into all fields`() {
        val json = """{"id":10,"data":"2026-10-30","horario":"11:00:00","status":"confirmado","profissional_id":1}"""

        val appointment = gson.fromJson(json, AppointmentsResponse::class.java)

        assertEquals(10L, appointment.id)
        assertEquals("2026-10-30", appointment.data)
        assertEquals("11:00:00", appointment.horario)
        assertEquals("confirmado", appointment.status)
        assertEquals(1L, appointment.profissionalId)
    }

    @Test
    fun `deserializes missing professional id as null`() {
        val json = """{"id":10,"data":"2026-10-30","horario":"11:00:00","status":"pendente"}"""

        val appointment = gson.fromJson(json, AppointmentsResponse::class.java)

        assertNull(appointment.profissionalId)
    }

    @Test
    fun `constructor applies default values`() {
        val appointment = AppointmentsResponse(id = 10, data = "2026-10-30", horario = "11:00:00", status = "pendente")

        assertNull(appointment.profissionalId)
    }

    @Test
    fun `serializes using snake case field names`() {
        val appointment = AppointmentsResponse(10, "2026-10-30", "11:00:00", "confirmado", 1)

        val json = JsonParser.parseString(gson.toJson(appointment)).asJsonObject

        assertEquals(1L, json["profissional_id"].asLong)
        assertFalse(json.has("profissionalId"))
    }
}
