package com.glowbook.android.ui.features.calendar.model

import com.glowbook.android.domain.models.BookedSlotEntity
import com.glowbook.android.domain.models.ProfessionalCalendarEntity
import com.glowbook.android.domain.models.ProfessionalEntity
import com.glowbook.android.domain.models.ServiceEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CalendarUiModelsTest {

    private val friday = LocalDate.of(2026, 10, 30)
    private val saturday = LocalDate.of(2026, 10, 31)

    private fun service(id: Long, name: String, minutes: Int) = ServiceEntity(id, name, 100.0, minutes, null, 1)

    private fun calendar(services: List<ServiceEntity>) = ProfessionalCalendarEntity(
        professional = ProfessionalEntity(1, "Camila Rocha", "Cabeleireira", 4.9, null, null, true, 1),
        services = services,
        bookedSlots = emptyList()
    )

    @Test
    fun `toDayLabel formats short weekday day and month`() {
        assertEquals("Sex, 30 Out", friday.toDayLabel())
        assertEquals("Seg, 2 Nov", LocalDate.of(2026, 11, 2).toDayLabel())
    }

    @Test
    fun `toCalendarDaysUi marks days with bookings`() {
        val days = listOf(friday, saturday).toCalendarDaysUi(
            listOf(BookedSlotEntity(friday, LocalTime.of(11, 0)))
        )

        assertEquals(
            listOf(
                CalendarDayUi(friday, "SEX", "30", hasBookings = true),
                CalendarDayUi(saturday, "SÁB", "31", hasBookings = false)
            ),
            days
        )
    }

    @Test
    fun `timeSlotsFor lists working hours`() {
        val slots = timeSlotsFor(saturday, emptyList(), now = LocalDateTime.of(2026, 10, 27, 8, 0))

        assertEquals(
            listOf("08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00", "18:00"),
            slots.map { it.label }
        )
        assertTrue(slots.none { it.taken })
        assertEquals(LocalTime.of(8, 0), slots.first().time)
    }

    @Test
    fun `timeSlotsFor uses current time by default`() {
        val yesterday = LocalDate.now().minusDays(1)

        val slots = timeSlotsFor(yesterday, emptyList())

        assertEquals(9, slots.size)
        assertTrue(slots.all { it.taken })
    }

    @Test
    fun `timeSlotsFor marks slots with booking starting inside the hour`() {
        val booked = listOf(
            BookedSlotEntity(friday, LocalTime.of(11, 0)),
            BookedSlotEntity(friday, LocalTime.of(15, 30)),
            BookedSlotEntity(saturday, LocalTime.of(16, 0))
        )

        val taken = timeSlotsFor(friday, booked, now = LocalDateTime.of(2026, 10, 27, 8, 0))
            .filter { it.taken }
            .map { it.label }

        assertEquals(listOf("11:00", "15:00"), taken)
    }

    @Test
    fun `timeSlotsFor marks past and current slots as taken`() {
        val slots = timeSlotsFor(friday, emptyList(), now = LocalDateTime.of(2026, 10, 30, 9, 0))

        assertTrue(slots.first { it.label == "08:00" }.taken)
        assertTrue(slots.first { it.label == "09:00" }.taken)
        assertFalse(slots.first { it.label == "10:00" }.taken)
    }

    @Test
    fun `summary with selected time`() {
        val summary = calendar(listOf(service(1, "Corte", 45), service(2, "Escova", 90)))
            .toSummaryUi(friday, LocalTime.of(14, 0))

        assertEquals(
            BookingSummaryUi(
                professional = "Camila Rocha",
                services = "Corte + Escova",
                dateTime = "Sex, 30 Out • 14:00",
                duration = "~2h 15min"
            ),
            summary
        )
    }

    @Test
    fun `summary without time asks to choose one`() {
        val summary = calendar(listOf(service(1, "Corte", 60))).toSummaryUi(friday, null)

        assertEquals("Sex, 30 Out • escolha um horário", summary.dateTime)
        assertEquals("~1h", summary.duration)
    }

    @Test
    fun `summary without services shows dash and zero minutes`() {
        val summary = calendar(emptyList()).toSummaryUi(friday, null)

        assertEquals("—", summary.services)
        assertEquals("~0min", summary.duration)
    }

    @Test
    fun `summary with less than one hour shows minutes only`() {
        assertEquals("~30min", calendar(listOf(service(1, "Sobrancelha", 30))).toSummaryUi(friday, null).duration)
    }
}
