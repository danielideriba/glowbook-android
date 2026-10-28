package com.glowbook.android.data.remote.model

import com.glowbook.android.di.NetworkModule
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfessionalsResponseTest {

    private val gson = NetworkModule.provideGson()

    @Test
    fun `deserializes supabase json into all fields`() {
        val json = """
            {
              "id": 1,
              "nome": "Camila Rocha",
              "especialidade": "Cabeleireira",
              "avaliacao": 4.9,
              "avatar": "👩",
              "cor_fundo": "#FDE8EC",
              "disponivel": false,
              "categoria_id": 2
            }
        """.trimIndent()

        val professional = gson.fromJson(json, ProfessionalsResponse::class.java)

        assertEquals(1L, professional.id)
        assertEquals("Camila Rocha", professional.nome)
        assertEquals("Cabeleireira", professional.especialidade)
        assertEquals(4.9, professional.avaliacao, 0.0)
        assertEquals("👩", professional.avatar)
        assertEquals("#FDE8EC", professional.corFundo)
        assertFalse(professional.disponivel)
        assertEquals(2L, professional.categoriaId)
    }

    @Test
    fun `deserializes missing optional fields as null`() {
        val json = """{"id":1,"nome":"Camila","especialidade":"Cabelo","avaliacao":4.9,"disponivel":true,"categoria_id":2}"""

        val professional = gson.fromJson(json, ProfessionalsResponse::class.java)

        assertNull(professional.avatar)
        assertNull(professional.corFundo)
    }

    @Test
    fun `constructor applies default values`() {
        val professional = ProfessionalsResponse(
            id = 1,
            nome = "Camila",
            especialidade = "Cabelo",
            avaliacao = 4.9,
            categoriaId = 2
        )

        assertNull(professional.avatar)
        assertNull(professional.corFundo)
        assertTrue(professional.disponivel)
    }

    @Test
    fun `serializes using snake case field names`() {
        val professional = ProfessionalsResponse(1, "Camila", "Cabelo", 4.9, "👩", "#FFF", true, 2)

        val json = JsonParser.parseString(gson.toJson(professional)).asJsonObject

        assertEquals("#FFF", json["cor_fundo"].asString)
        assertEquals(2L, json["categoria_id"].asLong)
        assertFalse(json.has("corFundo"))
        assertFalse(json.has("categoriaId"))
    }
}
