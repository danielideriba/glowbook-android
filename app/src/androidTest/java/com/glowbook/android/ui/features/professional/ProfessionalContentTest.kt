package com.glowbook.android.ui.features.professional

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
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
import com.glowbook.android.R
import com.glowbook.android.ui.common.theme.GlowbookTheme
import com.glowbook.android.ui.components.BottomNavItem
import com.glowbook.android.ui.features.home.model.ProfessionalUi
import com.glowbook.android.ui.features.professional.components.ServiceItemCard
import com.glowbook.android.ui.features.professional.model.ServiceUi
import com.glowbook.android.utils.UiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfessionalContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val camila = ProfessionalUi(1, "👩", Color(0xFFFDE8EC), "Camila Rocha", "Cabeleireira", 4.9, R.string.professional_available_today)
    private val corte = ServiceUi(1, "✂️", "Corte", "⏱ 45 min", "R$ 120", 120.0, 45)
    private val escova = ServiceUi(2, "💨", "Escova", "⏱ 90 min", "R$ 140", 140.0, 90)
    private val coloracao = ServiceUi(3, "🎨", "Coloração", "⏱ 60 min", "R$ 180,50", 180.5, 60)
    private val services = listOf(corte, escova, coloracao)

    private fun setContent(
        professional: ProfessionalUi? = camila,
        servicesState: UiState<List<ServiceUi>> = UiState.Success(services),
        selected: Set<Long> = emptySet(),
        onBackClick: () -> Unit = {},
        onContinueClick: () -> Unit = {},
        onBottomNavClick: (BottomNavItem) -> Unit = {}
    ) {
        composeRule.setContent {
            GlowbookTheme {
                // Same selection rules as ProfessionalServicesViewModel.
                var selectedIds by remember { mutableStateOf(selected) }
                ProfessionalContent(
                    professional = professional,
                    servicesState = servicesState,
                    selectedServiceIds = selectedIds,
                    onBackClick = onBackClick,
                    onServiceClick = { service ->
                        selectedIds = if (service.id in selectedIds) selectedIds - service.id else selectedIds + service.id
                    },
                    onSelectAllClick = {
                        val all = services.map { it.id }.toSet()
                        selectedIds = if (selectedIds.containsAll(all)) emptySet() else all
                    },
                    onContinueClick = onContinueClick,
                    onBottomNavClick = onBottomNavClick
                )
            }
        }
    }

    @Test
    fun showsProfessionalHeaderAndServices() {
        setContent()

        composeRule.onNodeWithText("Camila Rocha").assertIsDisplayed()
        composeRule.onNodeWithText("★ 4.9 • Cabeleireira").assertIsDisplayed()
        composeRule.onNodeWithText("ESCOLHA OS SERVIÇOS").assertIsDisplayed()
        composeRule.onNodeWithText("Corte").assertIsDisplayed()
        composeRule.onNodeWithText("⏱ 90 min").assertIsDisplayed()
        composeRule.onNodeWithText("R$ 180,50").assertIsDisplayed()
    }

    @Test
    fun startsWithNothingSelectedAndContinueDisabled() {
        setContent()

        composeRule.onNodeWithText("0 serviços • 0min").assertIsDisplayed()
        composeRule.onNodeWithText("R$ 0,00").assertIsDisplayed()
        composeRule.onNodeWithText("Escolher horário →").assertIsNotEnabled()
        composeRule.onAllNodesWithText("✓").assertCountEquals(0)
    }

    @Test
    fun selectingServicesUpdatesTotalAndEnablesContinue() {
        setContent()

        composeRule.onNodeWithText("Corte").performClick()
        composeRule.onNodeWithText("1 serviço • 45min").assertIsDisplayed()
        composeRule.onNodeWithText("R$ 120,00").assertIsDisplayed()

        composeRule.onNodeWithText("Escova").performClick()
        composeRule.onNodeWithText("2 serviços • 2h 15min").assertIsDisplayed()
        composeRule.onNodeWithText("R$ 260,00").assertIsDisplayed()
        composeRule.onAllNodesWithText("✓").assertCountEquals(2)
        composeRule.onNodeWithText("Escolher horário →").assertIsEnabled()
    }

    @Test
    fun tappingSelectedServiceUnselectsIt() {
        setContent(selected = setOf(1, 2))

        composeRule.onNodeWithText("Corte").performClick()

        composeRule.onNodeWithText("1 serviço • 1h 30min").assertIsDisplayed()
        composeRule.onAllNodesWithText("✓").assertCountEquals(1)
    }

    @Test
    fun selectAllTogglesEveryService() {
        setContent()

        composeRule.onNodeWithText("Selecionar todos").performClick()
        composeRule.onNodeWithText("3 serviços • 3h 15min").assertIsDisplayed()
        composeRule.onNodeWithText("R$ 440,50").assertIsDisplayed()
        composeRule.onNodeWithText("✓ Todos").assertIsDisplayed()

        composeRule.onNodeWithText("✓ Todos").performClick()
        composeRule.onNodeWithText("Selecionar todos").assertIsDisplayed()
        composeRule.onNodeWithText("0 serviços • 0min").assertIsDisplayed()
    }

    @Test
    fun continueAndBackAreReported() {
        var continueClicks = 0
        var backClicks = 0
        setContent(selected = setOf(1), onContinueClick = { continueClicks++ }, onBackClick = { backClicks++ })

        composeRule.onNodeWithText("Escolher horário →").performClick()
        composeRule.onNodeWithText("←").performClick()

        assertEquals(1, continueClicks)
        assertEquals(1, backClicks)
    }

    @Test
    fun showsEmptyMessageWithoutServices() {
        setContent(servicesState = UiState.Success(emptyList()))

        composeRule.onNodeWithText("Nenhum serviço disponível para este profissional.").assertIsDisplayed()
        composeRule.onNodeWithText("Escolher horário →").assertIsNotEnabled()
    }

    @Test
    fun showsErrorMessage() {
        setContent(servicesState = UiState.Error("Resources not found. Please check the spelling and try again."))

        composeRule.onNodeWithText("Resources not found. Please check the spelling and try again.").assertIsDisplayed()
    }

    @Test
    fun showsLoadingWithoutProfessional() {
        setContent(professional = null, servicesState = UiState.Loading)

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        composeRule.onAllNodesWithText("Camila Rocha").assertCountEquals(0)
        composeRule.onNodeWithText("←").assertIsDisplayed()
    }

    @Test
    fun bottomNavShowsScheduleAndForwardsClicks() {
        val clicked = mutableListOf<BottomNavItem>()
        setContent(onBottomNavClick = { clicked += it })

        composeRule.onNodeWithText("Início").performClick()

        assertEquals(listOf(BottomNavItem.Home), clicked)
    }

    @Test
    fun serviceItemCard_showsCheckOnlyWhenSelected() {
        composeRule.setContent {
            GlowbookTheme {
                var selected by remember { mutableStateOf(false) }
                ServiceItemCard(service = corte, selected = selected, onClick = { selected = !selected })
            }
        }

        composeRule.onAllNodesWithText("✓").assertCountEquals(0)
        composeRule.onNodeWithText("Corte").performClick()
        composeRule.onNodeWithText("✓").assertIsDisplayed()
    }
}
