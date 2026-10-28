package com.glowbook.android.data.remote.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class AppointmentWithServicesTest {

    private val services = listOf(
        ServicesResponse(id = 1, nome = "Corte", categoriaId = 1),
        ServicesResponse(id = 2, nome = "Escova", categoriaId = 1)
    )

    @Test
    fun `up next holds appointment and services`() {
        val appointment = AppointmentUpNextResponse(5, 2, "2026-10-30", "14:00:00", "confirmado", 1, "Camila", null, 4.9, "Cabelo")

        val combined = AppointmentUpNextWithServices(appointment, services)

        assertSame(appointment, combined.appointment)
        assertEquals(services, combined.services)
    }

    @Test
    fun `history holds appointment and services`() {
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

        val combined = AppointmentHistoryWithServices(appointment, services)

        assertSame(appointment, combined.appointment)
        assertEquals(services, combined.services)
    }

    @Test
    fun `history accepts appointment without services`() {
        val appointment = AppointmentsHistoryResponse(
            id = 8,
            data = "2026-10-20",
            horario = "10:00:00",
            status = "cancelado",
            jaPassou = false,
            profissionalId = 2,
            profissional = "Bea",
            avaliacao = 4.7,
            categoria = "Unhas"
        )

        assertTrue(AppointmentHistoryWithServices(appointment, emptyList()).services.isEmpty())
    }
}
