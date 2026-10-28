package com.glowbook.android.data.remote.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfessionalWithServicesTest {

    private val professional = ProfessionalsResponse(
        id = 1,
        nome = "Camila Rocha",
        especialidade = "Cabeleireira",
        avaliacao = 4.9,
        categoriaId = 1
    )
    private val services = listOf(
        ServicesResponse(id = 1, nome = "Corte", categoriaId = 1, preco = 120.0),
        ServicesResponse(id = 2, nome = "Escova", categoriaId = 1, preco = 140.0)
    )

    @Test
    fun `holds professional and services`() {
        val combined = ProfessionalWithServices(professional, services)

        assertSame(professional, combined.professional)
        assertEquals(services, combined.services)
    }

    @Test
    fun `accepts professional without services`() {
        val combined = ProfessionalWithServices(professional, emptyList())

        assertTrue(combined.services.isEmpty())
    }
}
