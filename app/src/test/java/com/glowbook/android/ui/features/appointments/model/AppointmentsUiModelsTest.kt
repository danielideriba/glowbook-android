package com.glowbook.android.ui.features.appointments.model

import com.glowbook.android.R
import com.glowbook.android.domain.models.AppointmentEntity
import com.glowbook.android.ui.common.theme.GlowDanger
import com.glowbook.android.ui.common.theme.GlowRose
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class AppointmentsUiModelsTest {

    private fun appointment(
        status: String = "confirmado",
        isPast: Boolean = false,
        price: Double? = 260.0,
        avatar: String? = "🧑",
        date: LocalDate = LocalDate.of(2026, 10, 30),
        time: LocalTime = LocalTime.of(9, 5)
    ) = AppointmentEntity(
        id = 5,
        services = "Corte + Escova",
        price = price,
        durationMinutes = 135,
        date = date,
        time = time,
        status = status,
        professionalId = 1,
        professionalName = "Camila Rocha",
        professionalAvatar = avatar,
        professionalRating = 4.9,
        category = "Cabelo",
        isPast = isPast
    )

    @Test
    fun `maps entity to ui`() {
        assertEquals(
            AppointmentUi(
                id = 5,
                services = "Corte + Escova",
                professional = "🧑 Camila Rocha",
                status = AppointmentStatusUi.Confirmed,
                date = "Sex, 30 Out",
                time = "09:05",
                price = "R$ 260",
                tab = AppointmentsTab.Upcoming
            ),
            appointment().toUi()
        )
    }

    @Test
    fun `uses default avatar when missing`() {
        assertEquals("👩 Camila Rocha", appointment(avatar = null).toUi().professional)
    }

    @Test
    fun `formats price with cents in pt-BR`() {
        assertEquals("R$ 120,50", appointment(price = 120.5).toUi().price)
    }

    @Test
    fun `formats missing price as zero`() {
        assertEquals("R$ 0", appointment(price = null).toUi().price)
    }

    @Test
    fun `maps status strings ignoring case`() {
        assertEquals(AppointmentStatusUi.Confirmed, appointment(status = "CONFIRMADO").toUi().status)
        assertEquals(AppointmentStatusUi.Done, appointment(status = "concluido").toUi().status)
        assertEquals(AppointmentStatusUi.Done, appointment(status = "Concluído").toUi().status)
        assertEquals(AppointmentStatusUi.Cancelled, appointment(status = "cancelado").toUi().status)
        assertEquals(AppointmentStatusUi.Pending, appointment(status = "pendente").toUi().status)
        assertEquals(AppointmentStatusUi.Pending, appointment(status = "desconhecido").toUi().status)
    }

    @Test
    fun `chooses tab by status and past flag`() {
        assertEquals(AppointmentsTab.Upcoming, appointment(status = "confirmado").toUi().tab)
        assertEquals(AppointmentsTab.Upcoming, appointment(status = "pendente").toUi().tab)
        assertEquals(AppointmentsTab.History, appointment(status = "concluido").toUi().tab)
        assertEquals(AppointmentsTab.History, appointment(status = "confirmado", isPast = true).toUi().tab)
        assertEquals(AppointmentsTab.Cancelled, appointment(status = "cancelado").toUi().tab)
        assertEquals(AppointmentsTab.Cancelled, appointment(status = "cancelado", isPast = true).toUi().tab)
    }

    @Test
    fun `formats other weekdays and months`() {
        val ui = appointment(date = LocalDate.of(2026, 6, 28), time = LocalTime.of(14, 30)).toUi()

        assertEquals("Dom, 28 Jun", ui.date)
        assertEquals("14:30", ui.time)
    }

    @Test
    fun `tabs use string resources in order`() {
        assertEquals(
            listOf(R.string.tab_text_next, R.string.tab_text_history, R.string.tab_text_cancelled),
            AppointmentsTab.entries.map { it.label }
        )
    }

    @Test
    fun `statuses use string resources and colors`() {
        assertEquals(R.string.status_confirmed, AppointmentStatusUi.Confirmed.label)
        assertEquals(R.string.status_pending, AppointmentStatusUi.Pending.label)
        assertEquals(R.string.status_done, AppointmentStatusUi.Done.label)
        assertEquals(R.string.status_cancelled, AppointmentStatusUi.Cancelled.label)
        assertEquals(GlowRose, AppointmentStatusUi.Confirmed.stripe)
        assertEquals(GlowDanger, AppointmentStatusUi.Cancelled.badgeText)
    }
}
