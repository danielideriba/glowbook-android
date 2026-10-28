package com.glowbook.android.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.glowbook.android.domain.models.ProfessionalCalendarEntity
import com.glowbook.android.domain.usecase.CreateAppointmentUseCase
import com.glowbook.android.domain.usecase.FetchProfessionalCalendarUseCase
import com.glowbook.android.testutils.MainDispatcherRule
import com.glowbook.android.ui.nav.AppScreen
import com.glowbook.android.utils.UiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class CalendarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fetchCalendar: FetchProfessionalCalendarUseCase = mockk()
    private val createAppointment: CreateAppointmentUseCase = mockk()

    private fun viewModel(
        professionalId: Long? = 1,
        serviceIds: String? = "1,2"
    ): CalendarViewModel {
        val args = buildMap<String, Any> {
            professionalId?.let { put(AppScreen.Calendar.ARG_PROFESSIONAL_ID, it) }
            serviceIds?.let { put(AppScreen.Calendar.ARG_SERVICE_IDS, it) }
        }
        return CalendarViewModel(SavedStateHandle(args), fetchCalendar, createAppointment)
    }

    @Test
    fun `requires professional id argument`() {
        assertThrows(IllegalStateException::class.java) { viewModel(professionalId = null) }
    }

    @Test
    fun `calendar shows next six days without sundays`() {
        val days = viewModel().calendarDays

        assertEquals(6, days.size)
        assertTrue(days.none { it.dayOfWeek == DayOfWeek.SUNDAY })
        assertTrue(days.zipWithNext().all { (a, b) -> b.isAfter(a) })
        val today = LocalDate.now()
        val expectedFirst = if (today.dayOfWeek == DayOfWeek.SUNDAY) today.plusDays(1) else today
        assertEquals(expectedFirst, days.first())
    }

    @Test
    fun `initial state selects first day and no time`() {
        val viewModel = viewModel()

        assertEquals(viewModel.calendarDays.first(), viewModel.selectedDate.value)
        assertNull(viewModel.selectedTime.value)
        assertEquals(UiState.Ready, viewModel.calendarState.value)
        assertEquals(UiState.Ready, viewModel.createAppointmentState.value)
    }

    @Test
    fun `fetchProfessionalCalendar uses navigation args and calendar range`() {
        val calendar = UiState.Success(ProfessionalCalendarEntity(professionalEntity(), emptyList(), emptyList()))
        every { fetchCalendar.invoke(any(), any(), any(), any()) } returns flowOf(calendar)
        val viewModel = viewModel(serviceIds = " 1, 2 ,x,")

        viewModel.fetchProfessionalCalendar()

        verify(exactly = 1) {
            fetchCalendar.invoke(1, listOf(1, 2), viewModel.calendarDays.first(), viewModel.calendarDays.last())
        }
        assertEquals(calendar, viewModel.calendarState.value)
    }

    @Test
    fun `missing service ids become empty list`() {
        every { fetchCalendar.invoke(any(), any(), any(), any()) } returns flowOf(UiState.Loading)

        viewModel(serviceIds = null).fetchProfessionalCalendar()

        verify { fetchCalendar.invoke(1, emptyList(), any(), any()) }
    }

    @Test
    fun `fetchProfessionalCalendar exception becomes error state`() {
        every { fetchCalendar.invoke(any(), any(), any(), any()) } returns flow { throw IOException("boom") }
        val viewModel = viewModel()

        viewModel.fetchProfessionalCalendar()

        assertEquals(UiState.Error("boom"), viewModel.calendarState.value)
    }

    @Test
    fun `selectTime stores time`() {
        val viewModel = viewModel()

        viewModel.selectTime(LocalTime.of(14, 0))

        assertEquals(LocalTime.of(14, 0), viewModel.selectedTime.value)
    }

    @Test
    fun `selecting another date clears the time`() {
        val viewModel = viewModel()
        viewModel.selectTime(LocalTime.of(14, 0))

        viewModel.selectDate(viewModel.calendarDays[1])

        assertEquals(viewModel.calendarDays[1], viewModel.selectedDate.value)
        assertNull(viewModel.selectedTime.value)
    }

    @Test
    fun `selecting the same date keeps the time`() {
        val viewModel = viewModel()
        viewModel.selectTime(LocalTime.of(14, 0))

        viewModel.selectDate(viewModel.calendarDays.first())

        assertEquals(LocalTime.of(14, 0), viewModel.selectedTime.value)
    }

    @Test
    fun `createAppointment does nothing without a selected time`() {
        val viewModel = viewModel()

        viewModel.createAppointment()

        verify(exactly = 0) { createAppointment.invoke(any(), any(), any(), any()) }
        assertEquals(UiState.Ready, viewModel.createAppointmentState.value)
    }

    @Test
    fun `createAppointment sends selection and stores created id`() {
        every { createAppointment.invoke(any(), any(), any(), any()) } returns
            flowOf(UiState.Loading, UiState.Success(42L))
        val viewModel = viewModel()
        val date = viewModel.calendarDays[2]
        viewModel.selectDate(date)
        viewModel.selectTime(LocalTime.of(15, 0))

        viewModel.createAppointment()

        verify(exactly = 1) { createAppointment.invoke(1, listOf(1, 2), date, LocalTime.of(15, 0)) }
        assertEquals(UiState.Success(42L), viewModel.createAppointmentState.value)
    }

    @Test
    fun `createAppointment ignores taps while loading`() {
        every { createAppointment.invoke(any(), any(), any(), any()) } returns flowOf(UiState.Loading)
        val viewModel = viewModel()
        viewModel.selectTime(LocalTime.of(15, 0))

        viewModel.createAppointment()
        viewModel.createAppointment()

        verify(exactly = 1) { createAppointment.invoke(any(), any(), any(), any()) }
        assertEquals(UiState.Loading, viewModel.createAppointmentState.value)
    }

    @Test
    fun `createAppointment exception becomes error state`() {
        every { createAppointment.invoke(any(), any(), any(), any()) } returns flow { throw IOException("offline") }
        val viewModel = viewModel()
        viewModel.selectTime(LocalTime.of(15, 0))

        viewModel.createAppointment()

        assertEquals(UiState.Error("offline"), viewModel.createAppointmentState.value)
    }

    @Test
    fun `clearCreateAppointmentState resets to ready`() {
        every { createAppointment.invoke(any(), any(), any(), any()) } returns flowOf(UiState.Error("rejected"))
        val viewModel = viewModel()
        viewModel.selectTime(LocalTime.of(15, 0))
        viewModel.createAppointment()

        viewModel.clearCreateAppointmentState()

        assertEquals(UiState.Ready, viewModel.createAppointmentState.value)
    }
}
