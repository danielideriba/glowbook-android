package com.glowbook.android.ui.features.calendar.model

import com.glowbook.android.domain.models.BookedSlotEntity
import com.glowbook.android.domain.models.ProfessionalCalendarEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import com.glowbook.android.utils.LocaleConstants.PT_BR

data class CalendarDayUi(
    val date: LocalDate,
    val dayOfWeek: String,
    val dayNumber: String,
    val hasBookings: Boolean
)

data class TimeSlotUi(
    val time: LocalTime,
    val label: String,
    val taken: Boolean
)

data class BookingSummaryUi(
    val professional: String,
    val services: String,
    val dateTime: String,
    val duration: String
)

private val workingHours = listOf(8, 9, 10, 11, 14, 15, 16, 17, 18).map { LocalTime.of(it, 0) }
private const val SLOT_MINUTES = 60L

private fun LocalTime.toLabel(): String = "%02d:%02d".format(hour, minute)

private fun Int.toDurationLabel(): String {
    val hours = this / 60
    val minutes = this % 60
    return when {
        hours == 0 -> "${minutes}min"
        minutes == 0 -> "${hours}h"
        else -> "${hours}h ${minutes}min"
    }
}

fun LocalDate.toDayLabel(): String {
    val weekDay = dayOfWeek.getDisplayName(TextStyle.SHORT, PT_BR)
        .removeSuffix(".")
        .replaceFirstChar { it.titlecase(PT_BR) }
    val month = month.getDisplayName(TextStyle.SHORT, PT_BR)
        .removeSuffix(".")
        .replaceFirstChar { it.titlecase(PT_BR) }
    return "$weekDay, $dayOfMonth $month"
}

fun List<LocalDate>.toCalendarDaysUi(bookedSlots: List<BookedSlotEntity>): List<CalendarDayUi> {
    val bookedDates = bookedSlots.map { it.date }.toSet()
    return map { date ->
        CalendarDayUi(
            date = date,
            dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, PT_BR)
                .removeSuffix(".")
                .uppercase(PT_BR),
            dayNumber = date.dayOfMonth.toString(),
            hasBookings = date in bookedDates
        )
    }
}

fun timeSlotsFor(
    date: LocalDate,
    bookedSlots: List<BookedSlotEntity>,
    now: LocalDateTime = LocalDateTime.now()
): List<TimeSlotUi> {
    val bookedTimes = bookedSlots.filter { it.date == date }.map { it.time }
    return workingHours.map { start ->
        val end = start.plusMinutes(SLOT_MINUTES)
        val booked = bookedTimes.any { !it.isBefore(start) && it.isBefore(end) }
        val past = !LocalDateTime.of(date, start).isAfter(now)
        TimeSlotUi(time = start, label = start.toLabel(), taken = booked || past)
    }
}

fun ProfessionalCalendarEntity.toSummaryUi(
    selectedDate: LocalDate,
    selectedTime: LocalTime?
) = BookingSummaryUi(
    professional = professional.name,
    services = services.joinToString(" + ") { it.name }.ifEmpty { "—" },
    dateTime = selectedTime
        ?.let { "${selectedDate.toDayLabel()} • ${it.toLabel()}" }
        ?: "${selectedDate.toDayLabel()} • escolha um horário",
    duration = "~${services.sumOf { it.durationMinutes }.toDurationLabel()}"
)
