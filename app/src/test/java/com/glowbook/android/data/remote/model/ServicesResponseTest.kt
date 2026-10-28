package com.glowbook.android.data.remote.model

import com.glowbook.android.di.NetworkModule
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ServicesResponseTest {

    private val gson = NetworkModule.provideGson()

    @Test
    fun `deserializes supabase json into all fields`() {
        val json = """
            {
              "id": 3,
              "nome": "Coloração",
              "categoria_id": 1,
              "preco": 180.5,
              "duracao_minutos": 90,
              "ativo": false,
              "icone": "color-palette"
            }
        """.trimIndent()

        val service = gson.fromJson(json, ServicesResponse::class.java)

        assertEquals(3L, service.id)
        assertEquals("Coloração", service.nome)
        assertEquals(1L, service.categoriaId)
        assertEquals(180.5, service.preco!!, 0.0)
        assertEquals(90, service.duracaoMinutos)
        assertFalse(service.ativo)
        assertEquals("color-palette", service.icone)
    }

    @Test
    fun `deserializes missing optional fields as null`() {
        val json = """{"id":3,"nome":"Coloração","categoria_id":1,"ativo":true}"""

        val service = gson.fromJson(json, ServicesResponse::class.java)

        assertNull(service.preco)
        assertNull(service.duracaoMinutos)
        assertNull(service.icone)
    }

    @Test
    fun `constructor applies default values`() {
        val service = ServicesResponse(id = 3, nome = "Coloração", categoriaId = 1)

        assertNull(service.preco)
        assertNull(service.duracaoMinutos)
        assertTrue(service.ativo)
        assertNull(service.icone)
    }

    @Test
    fun `serializes using snake case field names`() {
        val service = ServicesResponse(3, "Coloração", 1, 180.5, 90, true, "color-palette")

        val json = JsonParser.parseString(gson.toJson(service)).asJsonObject

        assertEquals(1L, json["categoria_id"].asLong)
        assertEquals(90, json["duracao_minutos"].asInt)
        assertFalse(json.has("categoriaId"))
        assertFalse(json.has("duracaoMinutos"))
    }
}
