package com.glowbook.android.ui.features.calendar

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.glowbook.android.R
import com.glowbook.android.ui.common.theme.GlowBackground
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowRose
import com.glowbook.android.ui.components.BottomNavItem
import com.glowbook.android.ui.components.GlowBottomNavBar
import com.glowbook.android.ui.components.SectionLabel
import com.glowbook.android.ui.features.calendar.components.BookingSummaryCard
import com.glowbook.android.ui.features.calendar.components.CalendarHeader
import com.glowbook.android.ui.features.calendar.components.ConfirmBookingFooter
import com.glowbook.android.ui.features.calendar.components.TimeSlotsGrid
import com.glowbook.android.ui.features.calendar.model.BookingSummaryUi
import com.glowbook.android.ui.features.calendar.model.CalendarDayUi
import com.glowbook.android.ui.features.calendar.model.TimeSlotUi
import com.glowbook.android.ui.features.calendar.model.timeSlotsFor
import com.glowbook.android.ui.features.calendar.model.toCalendarDaysUi
import com.glowbook.android.ui.features.calendar.model.toDayLabel
import com.glowbook.android.ui.features.calendar.model.toSummaryUi
import com.glowbook.android.ui.nav.AppScreen
import com.glowbook.android.ui.viewmodel.CalendarViewModel
import com.glowbook.android.utils.UiState
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val calendarState by viewModel.calendarState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()
    val createAppointmentState by viewModel.createAppointmentState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchProfessionalCalendar()
    }

    LaunchedEffect(createAppointmentState) {
        when (val state = createAppointmentState) {
            is UiState.Success -> {
                Toast.makeText(context, R.string.create_appointment_success, Toast.LENGTH_SHORT).show()
                viewModel.clearCreateAppointmentState()
                navController.navigate(AppScreen.Appointments.route) {
                    popUpTo(AppScreen.Home.route)
                }
            }

            is UiState.Error -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.create_appointment_error, state.message),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.clearCreateAppointmentState()
            }

            UiState.Loading, UiState.Ready -> Unit
        }
    }

    val calendar = (calendarState as? UiState.Success)?.data
    val bookedSlots = calendar?.bookedSlots.orEmpty()

    CalendarContent(
        days = viewModel.calendarDays.toCalendarDaysUi(bookedSlots),
        selectedDate = selectedDate,
        selectedTime = selectedTime,
        slotsState = calendarState.mapSuccess { timeSlotsFor(selectedDate, it.bookedSlots) },
        summary = calendar?.toSummaryUi(selectedDate, selectedTime),
        onBackClick = { navController.popBackStack() },
        onDayClick = { viewModel.selectDate(it.date) },
        onSlotClick = { viewModel.selectTime(it.time) },
        creatingAppointment = createAppointmentState == UiState.Loading,
        onConfirmClick = { viewModel.createAppointment() },
        onBottomNavClick = { item ->
            when (item) {
                BottomNavItem.Home -> navController.popBackStack(AppScreen.Home.route, inclusive = false)
                BottomNavItem.Schedule -> navController.navigate(AppScreen.Appointments.route)
                else -> Unit
            }
        }
    )
}

@Composable
fun CalendarContent(
    days: List<CalendarDayUi>,
    selectedDate: LocalDate,
    selectedTime: LocalTime?,
    slotsState: UiState<List<TimeSlotUi>>,
    summary: BookingSummaryUi?,
    creatingAppointment: Boolean,
    onBackClick: () -> Unit,
    onDayClick: (CalendarDayUi) -> Unit,
    onSlotClick: (TimeSlotUi) -> Unit,
    onConfirmClick: () -> Unit,
    onBottomNavClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GlowBackground)
    ) {
        CalendarHeader(
            days = days,
            selectedDate = selectedDate,
            onDayClick = onDayClick,
            onBackClick = onBackClick
        )

        Box(modifier = Modifier.weight(1f)) {
            when (slotsState) {
                is UiState.Success -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 16.dp)
                ) {
                    SectionLabel(text = stringResource(R.string.calendar_section_available_times, selectedDate.toDayLabel()))
                    TimeSlotsGrid(
                        slots = slotsState.data,
                        selectedTime = selectedTime,
                        onSlotClick = onSlotClick
                    )
                    if (summary != null) {
                        Spacer(modifier = Modifier.height(20.dp))
                        BookingSummaryCard(summary = summary)
                    }
                }

                is UiState.Error -> CalendarMessage(text = slotsState.message)

                UiState.Loading, UiState.Ready -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GlowRose)
                }
            }
        }

        ConfirmBookingFooter(
            enabled = selectedTime != null,
            loading = creatingAppointment,
            onConfirmClick = onConfirmClick
        )

        GlowBottomNavBar(
            selected = BottomNavItem.Schedule,
            onItemClick = onBottomNavClick
        )
    }
}

@Composable
private fun CalendarMessage(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        fontSize = 13.sp,
        color = GlowMuted,
        textAlign = TextAlign.Center
    )
}

private fun <T, R> UiState<T>.mapSuccess(transform: (T) -> R): UiState<R> =
    when (this) {
        is UiState.Success -> UiState.Success(transform(data))
        is UiState.Error -> this
        UiState.Loading -> UiState.Loading
        UiState.Ready -> UiState.Ready
    }

//Mock to preview
@Preview(showBackground = true, heightDp = 800)
@Composable
private fun CalendarContentPreview() {
    val start = LocalDate.of(2026, 6, 23)
    val days = listOf("SEG", "TER", "QUA", "QUI", "SEX", "SÁB").mapIndexed { i, dow ->
        val date = start.plusDays(i.toLong())
        CalendarDayUi(date, dow, date.dayOfMonth.toString(), hasBookings = i == 2 || i == 4)
    }
    CalendarContent(
        days = days,
        selectedDate = days[3].date,
        selectedTime = LocalTime.of(14, 0),
        slotsState = UiState.Success(
            listOf(8, 9, 10, 11, 14, 15, 16, 17, 18).map {
                TimeSlotUi(LocalTime.of(it, 0), "%02d:00".format(it), taken = it == 8 || it == 10 || it == 16)
            }
        ),
        creatingAppointment = false,
        summary = BookingSummaryUi("Camila Rocha", "Corte feminino + Escova progressiva", "Qui, 26 Jun • 14:00", "~2h 15min"),
        onBackClick = {},
        onDayClick = {},
        onSlotClick = {},
        onConfirmClick = {},
        onBottomNavClick = {}
    )
}
