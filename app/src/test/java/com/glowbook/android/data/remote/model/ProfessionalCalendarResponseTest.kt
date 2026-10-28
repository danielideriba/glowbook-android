package com.glowbook.android.data.remote.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfessionalCalendarResponseTest {

    private val professional = ProfessionalsResponse(
        id = 1,
        nome = "Camila Rocha",
        especialidade = "Cabeleireira",
        avaliacao = 4.9,
        categoriaId = 1
    )
    private val services = listOf(ServicesResponse(id = 1, nome = "Corte", categoriaId = 1))
    private val appointments = listOf(
        AppointmentsResponse(id = 10, data = "2026-10-30", horario = "11:00:00", status = "confirmado", profissionalId = 1)
    )

    @Test
    fun `holds professional services and appointments`() {
        val calendar = ProfessionalCalendarResponse(professional, services, appointments)

        assertSame(professional, calendar.professional)
        assertEquals(services, calendar.services)
        assertEquals(appointments, calendar.appointments)
    }

    @Test
    fun `accepts calendar without booked appointments`() {
        val calendar = ProfessionalCalendarResponse(professional, services, emptyList())

        assertTrue(calendar.appointments.isEmpty())
    }
}
