package com.glowbook.android.ui.features.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.glowbook.android.ui.common.theme.GlowbookTheme
import com.glowbook.android.ui.components.BottomNavItem
import com.glowbook.android.ui.features.calendar.components.BookingSummaryCard
import com.glowbook.android.ui.features.calendar.model.BookingSummaryUi
import com.glowbook.android.ui.features.calendar.model.CalendarDayUi
import com.glowbook.android.ui.features.calendar.model.TimeSlotUi
import com.glowbook.android.utils.UiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class CalendarContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Mon 26 Oct 2026 .. Sat 31 Oct 2026
    private val days = listOf("SEG", "TER", "QUA", "QUI", "SEX", "SÁB").mapIndexed { i, dow ->
        val date = LocalDate.of(2026, 10, 26).plusDays(i.toLong())
        CalendarDayUi(date, dow, date.dayOfMonth.toString(), hasBookings = i == 4)
    }

    private val slots = listOf(8, 9, 10, 11, 14, 15, 16, 17, 18).map {
        TimeSlotUi(LocalTime.of(it, 0), "%02d:00".format(it), taken = it == 11 || it == 16)
    }

    private val summary = BookingSummaryUi("Camila Rocha", "Corte + Escova", "Sex, 30 Out • 14:00", "~2h 15min")

    private fun setContent(
        slotsState: UiState<List<TimeSlotUi>> = UiState.Success(slots),
        initialTime: LocalTime? = null,
        summary: BookingSummaryUi? = this.summary,
        creatingAppointment: Boolean = false,
        onDayClick: (CalendarDayUi) -> Unit = {},
        onSlotClick: (TimeSlotUi) -> Unit = {},
        onConfirmClick: () -> Unit = {},
        onBackClick: () -> Unit = {},
        onBottomNavClick: (BottomNavItem) -> Unit = {}
    ) {
        composeRule.setContent {
            GlowbookTheme {
                // Same selection rules as CalendarViewModel: new day clears the time.
                var selectedDate by remember { mutableStateOf(days[4].date) }
                var selectedTime by remember { mutableStateOf(initialTime) }
                CalendarContent(
                    days = days,
                    selectedDate = selectedDate,
                    selectedTime = selectedTime,
                    slotsState = slotsState,
                    summary = summary,
                    creatingAppointment = creatingAppointment,
                    onBackClick = onBackClick,
                    onDayClick = { day ->
                        onDayClick(day)
                        if (day.date != selectedDate) {
                            selectedDate = day.date
                            selectedTime = null
                        }
                    },
                    onSlotClick = { slot -> onSlotClick(slot); selectedTime = slot.time },
                    onConfirmClick = onConfirmClick,
                    onBottomNavClick = onBottomNavClick
                )
            }
        }
    }

    @Test
    fun showsHeaderDaysSlotsAndSummary() {
        setContent()

        composeRule.onNodeWithText("Escolha a data e horário").assertIsDisplayed()
        days.forEach {
            composeRule.onNodeWithText(it.dayOfWeek).assertIsDisplayed()
            composeRule.onNodeWithText(it.dayNumber).assertIsDisplayed()
        }
        composeRule.onNodeWithText("HORÁRIOS DISPONÍVEIS — SEX, 30 OUT").assertIsDisplayed()
        slots.forEach { composeRule.onNodeWithText(it.label).assertExists() }
        composeRule.onNodeWithText("Profissional").assertExists()
        composeRule.onNodeWithText("Corte + Escova").assertExists()
        composeRule.onNodeWithText("Sex, 30 Out • 14:00").assertExists()
        composeRule.onNodeWithText("~2h 15min").assertExists()
    }

    @Test
    fun takenSlotsAreDisabledAndFreeSlotsEnabled() {
        setContent()

        composeRule.onNodeWithText("11:00").assertIsNotEnabled()
        composeRule.onNodeWithText("16:00").assertIsNotEnabled()
        composeRule.onNodeWithText("14:00").assertIsEnabled()
    }

    @Test
    fun takenSlotClickIsIgnored() {
        val clicked = mutableListOf<TimeSlotUi>()
        setContent(onSlotClick = { clicked += it })

        composeRule.onNodeWithText("11:00").performClick()

        assertEquals(emptyList<TimeSlotUi>(), clicked)
    }

    @Test
    fun confirmIsDisabledUntilATimeIsChosen() {
        val clicked = mutableListOf<TimeSlotUi>()
        var confirms = 0
        setContent(onSlotClick = { clicked += it }, onConfirmClick = { confirms++ })

        composeRule.onNodeWithText("Confirmar agendamento").assertIsNotEnabled()
        composeRule.onNodeWithText("14:00").performClick()
        composeRule.onNodeWithText("Confirmar agendamento").assertIsEnabled().performClick()

        assertEquals(listOf(LocalTime.of(14, 0)), clicked.map { it.time })
        assertEquals(1, confirms)
    }

    @Test
    fun choosingAnotherDayClearsTheTime() {
        val days = mutableListOf<CalendarDayUi>()
        setContent(initialTime = LocalTime.of(14, 0), onDayClick = { days += it })

        composeRule.onNodeWithText("Confirmar agendamento").assertIsEnabled()
        composeRule.onNodeWithText("31").performClick()

        assertEquals(LocalDate.of(2026, 10, 31), days.single().date)
        composeRule.onNodeWithText("HORÁRIOS DISPONÍVEIS — SÁB, 31 OUT").assertIsDisplayed()
        composeRule.onNodeWithText("Confirmar agendamento").assertIsNotEnabled()
    }

    @Test
    fun creatingAppointmentShowsLoadingButton() {
        var confirms = 0
        setContent(initialTime = LocalTime.of(14, 0), creatingAppointment = true, onConfirmClick = { confirms++ })

        composeRule.onNodeWithText("Agendando…").assertIsNotEnabled().performClick()
        composeRule.onAllNodesWithText("Confirmar agendamento").assertCountEquals(0)
        assertEquals(0, confirms)
    }

    @Test
    fun hidesSummaryWhenNull() {
        setContent(summary = null)

        composeRule.onAllNodesWithText("Profissional").assertCountEquals(0)
    }

    @Test
    fun showsLoadingWhileSlotsLoad() {
        setContent(slotsState = UiState.Loading)
        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        composeRule.onAllNodesWithText("14:00").assertCountEquals(0)
    }

    @Test
    fun showsErrorMessage() {
        setContent(slotsState = UiState.Error("Sem conexão com a internet"))

        composeRule.onNodeWithText("Sem conexão com a internet").assertIsDisplayed()
        composeRule.onAllNodesWithText("14:00").assertCountEquals(0)
    }

    @Test
    fun backAndBottomNavAreReported() {
        var backs = 0
        val nav = mutableListOf<BottomNavItem>()
        setContent(onBackClick = { backs++ }, onBottomNavClick = { nav += it })

        composeRule.onNodeWithText("←").performClick()
        composeRule.onNodeWithText("Início").performClick()

        assertEquals(1, backs)
        assertEquals(listOf(BottomNavItem.Home), nav)
    }

    @Test
    fun bookingSummaryCard_showsLabelsAndValues() {
        composeRule.setContent { GlowbookTheme { BookingSummaryCard(summary = summary) } }

        listOf("Profissional", "Serviços", "Data e hora", "Duração estimada").forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
        listOf("Camila Rocha", "Corte + Escova", "Sex, 30 Out • 14:00", "~2h 15min").forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }
}
