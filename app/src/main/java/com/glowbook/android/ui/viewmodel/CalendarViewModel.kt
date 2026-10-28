package com.glowbook.android.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowbook.android.domain.models.ProfessionalCalendarEntity
import com.glowbook.android.domain.usecase.CreateAppointmentUseCase
import com.glowbook.android.domain.usecase.FetchProfessionalCalendarUseCase
import com.glowbook.android.ui.nav.AppScreen
import com.glowbook.android.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fetchProfessionalCalendarUseCase: FetchProfessionalCalendarUseCase,
    private val createAppointmentUseCase: CreateAppointmentUseCase
) : ViewModel() {
    private val professionalId: Long = checkNotNull(savedStateHandle[AppScreen.Calendar.ARG_PROFESSIONAL_ID])
    private val serviceIds: List<Long> = savedStateHandle.get<String>(AppScreen.Calendar.ARG_SERVICE_IDS)
        .orEmpty()
        .split(",")
        .mapNotNull { it.trim().toLongOrNull() }

    val calendarDays: List<LocalDate> = generateSequence(LocalDate.now()) { it.plusDays(1) }
        .filter { it.dayOfWeek != DayOfWeek.SUNDAY }
        .take(CALENDAR_DAYS)
        .toList()

    private val _calendarState = MutableStateFlow<UiState<ProfessionalCalendarEntity>>(UiState.Ready)
    val calendarState: StateFlow<UiState<ProfessionalCalendarEntity>> = _calendarState.asStateFlow()

    private val _selectedDate = MutableStateFlow(calendarDays.first())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _selectedTime = MutableStateFlow<LocalTime?>(null)
    val selectedTime: StateFlow<LocalTime?> = _selectedTime.asStateFlow()

    private val _createAppointmentState = MutableStateFlow<UiState<Long>>(UiState.Ready)
    val createAppointmentState: StateFlow<UiState<Long>> = _createAppointmentState.asStateFlow()

    fun fetchProfessionalCalendar() {
        viewModelScope.launch {
            fetchProfessionalCalendarUseCase.invoke(
                professionalId = professionalId,
                serviceIds = serviceIds,
                startDate = calendarDays.first(),
                endDate = calendarDays.last()
            )
                .catch { error -> _calendarState.value = UiState.Error("${error.localizedMessage}") }
                .collect { state ->
                    _calendarState.value = state
                }
        }
    }

    fun selectDate(date: LocalDate) {
        if (date == _selectedDate.value) return
        _selectedDate.value = date
        _selectedTime.value = null
    }

    fun selectTime(time: LocalTime) {
        _selectedTime.value = time
    }

    fun createAppointment() {
        val time = _selectedTime.value ?: return
        if (_createAppointmentState.value == UiState.Loading) return

        viewModelScope.launch {
            createAppointmentUseCase.invoke(
                professionalId = professionalId,
                serviceIds = serviceIds,
                date = _selectedDate.value,
                time = time
            )
                .catch { error -> _createAppointmentState.value = UiState.Error("${error.localizedMessage}") }
                .collect { state ->
                    _createAppointmentState.value = state
                }
        }
    }

    fun clearCreateAppointmentState() {
        _createAppointmentState.value = UiState.Ready
    }

    companion object {
        private const val CALENDAR_DAYS = 6
    }
}
