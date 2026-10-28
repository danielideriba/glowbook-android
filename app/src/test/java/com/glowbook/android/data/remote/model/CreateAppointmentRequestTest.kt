package com.glowbook.android.data.remote.model

import com.glowbook.android.di.NetworkModule
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class CreateAppointmentRequestTest {

    private val gson = NetworkModule.provideGson()

    private val request = CreateAppointmentRequest(
        data = "2026-10-30",
        horario = "14:00",
        profissionalId = 1,
        servicos = listOf(1, 2)
    )

    @Test
    fun `serializes rpc params with p prefix`() {
        val json = JsonParser.parseString(gson.toJson(request)).asJsonObject

        assertEquals(setOf("p_data", "p_horario", "p_profissional_id", "p_servicos"), json.keySet())
        assertEquals("2026-10-30", json["p_data"].asString)
        assertEquals("14:00", json["p_horario"].asString)
        assertEquals(1L, json["p_profissional_id"].asLong)
        assertEquals(listOf(1L, 2L), json["p_servicos"].asJsonArray.map { it.asLong })
    }

    @Test
    fun `serializes empty services list as empty array`() {
        val json = JsonParser.parseString(gson.toJson(request.copy(servicos = emptyList()))).asJsonObject

        assertEquals(0, json["p_servicos"].asJsonArray.size())
        assertFalse(json.has("servicos"))
    }

    @Test
    fun `deserializes rpc params back into request`() {
        val json = """{"p_data":"2026-10-30","p_horario":"14:00","p_profissional_id":1,"p_servicos":[1,2]}"""

        val parsed = gson.fromJson(json, CreateAppointmentRequest::class.java)

        assertEquals("2026-10-30", parsed.data)
        assertEquals("14:00", parsed.horario)
        assertEquals(1L, parsed.profissionalId)
        assertEquals(listOf(1L, 2L), parsed.servicos.map { it.toLong() })
    }
}
