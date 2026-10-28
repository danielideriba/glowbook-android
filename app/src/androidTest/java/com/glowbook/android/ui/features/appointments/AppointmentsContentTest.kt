package com.glowbook.android.ui.features.appointments

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.glowbook.android.ui.common.theme.GlowbookTheme
import com.glowbook.android.ui.components.BottomNavItem
import com.glowbook.android.ui.features.appointments.components.AppointmentCard
import com.glowbook.android.ui.features.appointments.model.AppointmentStatusUi
import com.glowbook.android.ui.features.appointments.model.AppointmentUi
import com.glowbook.android.ui.features.appointments.model.AppointmentsTab
import com.glowbook.android.utils.UiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppointmentsContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val upcoming = AppointmentUi(1, "Corte + Escova", "👩 Camila Rocha", AppointmentStatusUi.Confirmed, "Sex, 30 Out", "14:00", "R$ 260", AppointmentsTab.Upcoming)
    private val pending = AppointmentUi(2, "Nail Art", "🧑 Bea Lima", AppointmentStatusUi.Pending, "Sáb, 31 Out", "10:00", "R$ 120", AppointmentsTab.Upcoming)
    private val done = AppointmentUi(3, "Escova", "👩 Camila Rocha", AppointmentStatusUi.Done, "Ter, 20 Out", "09:00", "R$ 140", AppointmentsTab.History)
    private val cancelled = AppointmentUi(4, "Coloração", "👩 Camila Rocha", AppointmentStatusUi.Cancelled, "Qua, 21 Out", "16:00", "R$ 180,50", AppointmentsTab.Cancelled)

    private fun setContent(
        upNext: UiState<List<AppointmentUi>> = UiState.Success(listOf(upcoming, pending)),
        history: UiState<List<AppointmentUi>> = UiState.Success(listOf(done)),
        cancelledState: UiState<List<AppointmentUi>> = UiState.Success(listOf(cancelled)),
        onBackClick: () -> Unit = {},
        onBottomNavClick: (BottomNavItem) -> Unit = {}
    ) {
        composeRule.setContent {
            GlowbookTheme {
                AppointmentsContent(
                    appointmentsUpNextState = upNext,
                    appointmentsHistoryState = history,
                    cancelledState = cancelledState,
                    onBackClick = onBackClick,
                    onBottomNavClick = onBottomNavClick
                )
            }
        }
    }

    @Test
    fun showsTitleTabsAndUpcomingByDefault() {
        setContent()

        composeRule.onNodeWithText("Meus agendamentos").assertIsDisplayed()
        listOf("Próximos", "Histórico", "Cancelados").forEach { composeRule.onNodeWithText(it).assertIsDisplayed() }
        composeRule.onNodeWithText("Corte + Escova").assertIsDisplayed()
        composeRule.onNodeWithText("Nail Art").assertIsDisplayed()
        composeRule.onNodeWithText("Confirmado").assertIsDisplayed()
        composeRule.onNodeWithText("Pendente").assertIsDisplayed()
        composeRule.onAllNodesWithText("Escova").assertCountEquals(0)
    }

    @Test
    fun upcomingCardsShowMetaAndActions() {
        setContent(upNext = UiState.Success(listOf(upcoming)))

        composeRule.onNodeWithText("👩 Camila Rocha").assertIsDisplayed()
        composeRule.onNodeWithText("Sex, 30 Out").assertIsDisplayed()
        composeRule.onNodeWithText("14:00").assertIsDisplayed()
        composeRule.onNodeWithText("R$ 260").assertIsDisplayed()
        composeRule.onNodeWithText("Ver detalhes").assertIsDisplayed()
        composeRule.onNodeWithText("Reagendar").assertIsDisplayed()
        composeRule.onNodeWithText("Cancelar").assertIsDisplayed()
        composeRule.onAllNodesWithText("⭐ Avaliar").assertCountEquals(0)
    }

    @Test
    fun historyTabShowsDoneAppointmentsWithRateAction() {
        setContent()

        composeRule.onNodeWithText("Histórico").performClick()

        composeRule.onNodeWithText("Escova").assertIsDisplayed()
        composeRule.onNodeWithText("Concluído").assertIsDisplayed()
        composeRule.onNodeWithText("⭐ Avaliar").assertIsDisplayed()
        composeRule.onAllNodesWithText("Cancelar").assertCountEquals(0)
        composeRule.onAllNodesWithText("Corte + Escova").assertCountEquals(0)
    }

    @Test
    fun cancelledTabShowsOnlyRescheduleAction() {
        setContent()

        composeRule.onNodeWithText("Cancelados").performClick()

        composeRule.onNodeWithText("Coloração").assertIsDisplayed()
        composeRule.onNodeWithText("Cancelado").assertIsDisplayed()
        composeRule.onNodeWithText("Reagendar").assertIsDisplayed()
        composeRule.onAllNodesWithText("Cancelar").assertCountEquals(0)
        composeRule.onAllNodesWithText("⭐ Avaliar").assertCountEquals(0)
    }

    @Test
    fun emptyTabsShowTheirMessages() {
        setContent(
            upNext = UiState.Success(emptyList()),
            history = UiState.Success(emptyList()),
            cancelledState = UiState.Success(emptyList())
        )

        composeRule.onNodeWithText("Você não tem agendamentos próximos.").assertIsDisplayed()
        composeRule.onNodeWithText("Histórico").performClick()
        composeRule.onNodeWithText("Nenhum agendamento no histórico.").assertIsDisplayed()
        composeRule.onNodeWithText("Cancelados").performClick()
        composeRule.onNodeWithText("Nenhum agendamento cancelado.").assertIsDisplayed()
    }

    @Test
    fun eachTabShowsItsOwnLoadingOrError() {
        setContent(upNext = UiState.Loading, history = UiState.Error("Falha no histórico"))

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        composeRule.onNodeWithText("Histórico").performClick()
        composeRule.onNodeWithText("Falha no histórico").assertIsDisplayed()
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
    fun appointmentCard_reportsEachAction() {
        val events = mutableListOf<String>()
        composeRule.setContent {
            GlowbookTheme {
                AppointmentCard(
                    appointment = upcoming,
                    onClick = { events += "card" },
                    onDetailsClick = { events += "details" },
                    onRescheduleClick = { events += "reschedule" },
                    onCancelClick = { events += "cancel" },
                    onRateClick = { events += "rate" }
                )
            }
        }

        composeRule.onNodeWithText("Ver detalhes").performClick()
        composeRule.onNodeWithText("Reagendar").performClick()
        composeRule.onNodeWithText("Cancelar").performClick()
        composeRule.onNodeWithText("Corte + Escova").performClick()

        assertEquals(listOf("details", "reschedule", "cancel", "card"), events)
    }

    @Test
    fun appointmentCard_historyReportsRate() {
        val events = mutableListOf<String>()
        composeRule.setContent {
            GlowbookTheme {
                AppointmentCard(
                    appointment = done,
                    onClick = {},
                    onDetailsClick = {},
                    onRescheduleClick = { events += "reschedule" },
                    onCancelClick = {},
                    onRateClick = { events += "rate" }
                )
            }
        }

        composeRule.onNodeWithText("⭐ Avaliar").performClick()
        composeRule.onNodeWithText("Reagendar").performClick()

        assertEquals(listOf("rate", "reschedule"), events)
    }
}
