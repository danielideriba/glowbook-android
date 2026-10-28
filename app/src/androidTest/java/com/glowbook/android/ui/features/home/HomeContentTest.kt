package com.glowbook.android.ui.features.home

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.glowbook.android.R
import com.glowbook.android.ui.common.theme.GlowbookTheme
import com.glowbook.android.ui.components.BottomNavItem
import com.glowbook.android.ui.features.home.components.CategoriesRow
import com.glowbook.android.ui.features.home.components.HomeHeader
import com.glowbook.android.ui.features.home.components.NextAppointmentCard
import com.glowbook.android.ui.features.home.components.ProfessionalCard
import com.glowbook.android.ui.features.home.model.CategoryUi
import com.glowbook.android.ui.features.home.model.NextAppointmentUi
import com.glowbook.android.ui.features.home.model.ProfessionalUi
import com.glowbook.android.utils.UiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val camila = ProfessionalUi(1, "👩", Color(0xFFFDE8EC), "Camila Rocha", "Cabeleireira", 4.9, R.string.professional_available_today)
    private val bea = ProfessionalUi(2, "🧑", Color(0xFFE8F0FD), "Bea Lima", "Manicure", 3.0, R.string.professional_unavailable)
    private val corte = NextAppointmentUi("✂️", "Corte + Escova", "Sexta, 30 Out • 14:00 • Camila")
    private val nails = NextAppointmentUi("💅", "Nail Art", "Sábado, 31 Out • 10:00 • Bea")
    private val categories = listOf(CategoryUi("✂️", "Cabelo"), CategoryUi("💅", "Unhas"))

    private val progress = hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)

    private fun setHome(
        nextAppointments: List<NextAppointmentUi> = listOf(corte),
        categoriesState: UiState<List<CategoryUi>> = UiState.Success(categories),
        professionalsState: UiState<List<ProfessionalUi>> = UiState.Success(listOf(camila, bea)),
        onNextAppointmentClick: (NextAppointmentUi) -> Unit = {},
        onProfessionalClick: (ProfessionalUi) -> Unit = {},
        onBottomNavClick: (BottomNavItem) -> Unit = {}
    ) {
        composeRule.setContent {
            GlowbookTheme {
                HomeContent(
                    userName = "Ana",
                    nextAppointments = nextAppointments,
                    categoriesState = categoriesState,
                    professionalsState = professionalsState,
                    onNextAppointmentClick = onNextAppointmentClick,
                    onProfessionalClick = onProfessionalClick,
                    onBottomNavClick = onBottomNavClick
                )
            }
        }
    }

    @Test
    fun showsGreetingSectionsAndContent() {
        setHome()

        composeRule.onNodeWithText("Olá, Ana 👋").assertIsDisplayed()
        composeRule.onNodeWithText("O que vamos fazer hoje?").assertIsDisplayed()
        composeRule.onNodeWithText("SERVIÇOS").assertIsDisplayed()
        composeRule.onNodeWithText("PROFISSIONAIS DISPONÍVEIS").assertIsDisplayed()
        composeRule.onNodeWithText("Cabelo").assertIsDisplayed()
        composeRule.onNodeWithText("Unhas").assertIsDisplayed()
        composeRule.onNodeWithText("Camila Rocha").assertIsDisplayed()
        composeRule.onNodeWithText("Bea Lima").assertIsDisplayed()
        composeRule.onNodeWithText("Corte + Escova").assertIsDisplayed()
    }

    @Test
    fun hidesNextAppointmentCardWhenThereIsNone() {
        setHome(nextAppointments = emptyList())

        composeRule.onAllNodesWithText("PRÓXIMO AGENDAMENTO").assertCountEquals(0)
    }

    @Test
    fun showsSeveralNextAppointments() {
        setHome(nextAppointments = listOf(corte, nails))

        composeRule.onNodeWithText("Corte + Escova").assertIsDisplayed()
        composeRule.onNodeWithText("Nail Art").assertExists()
    }

    @Test
    fun showsLoadingWhileSectionsLoad() {
        setHome(categoriesState = UiState.Loading, professionalsState = UiState.Ready)

        composeRule.onAllNodes(progress).assertCountEquals(2)
        composeRule.onAllNodesWithText("Camila Rocha").assertCountEquals(0)
    }

    @Test
    fun showsErrorMessages() {
        setHome(
            categoriesState = UiState.Error("Falha nas categorias"),
            professionalsState = UiState.Error("Sem conexão com a internet")
        )

        composeRule.onNodeWithText("Falha nas categorias").assertIsDisplayed()
        composeRule.onNodeWithText("Sem conexão com a internet").assertIsDisplayed()
    }

    @Test
    fun professionalClickReportsProfessional() {
        val clicked = mutableListOf<ProfessionalUi>()
        setHome(onProfessionalClick = { clicked += it })

        composeRule.onNodeWithText("Bea Lima").performClick()

        assertEquals(listOf(bea), clicked)
    }

    @Test
    fun nextAppointmentClickReportsAppointment() {
        val clicked = mutableListOf<NextAppointmentUi>()
        setHome(onNextAppointmentClick = { clicked += it })

        composeRule.onNodeWithText("Corte + Escova").performClick()

        assertEquals(listOf(corte), clicked)
    }

    @Test
    fun bottomNavClickIsForwarded() {
        val clicked = mutableListOf<BottomNavItem>()
        setHome(onBottomNavClick = { clicked += it })

        composeRule.onNodeWithText("Agenda").performClick()

        assertEquals(listOf(BottomNavItem.Schedule), clicked)
    }

    @Test
    fun header_showsUserName() {
        composeRule.setContent {
            GlowbookTheme { HomeHeader(userName = "Maria", nextAppointments = emptyList(), onNextAppointmentClick = {}) }
        }

        composeRule.onNodeWithText("Olá, Maria 👋").assertIsDisplayed()
    }

    @Test
    fun nextAppointmentCard_showsAllTexts() {
        var clicks = 0
        composeRule.setContent { GlowbookTheme { NextAppointmentCard(appointment = corte, onClick = { clicks++ }) } }

        composeRule.onNodeWithText("PRÓXIMO AGENDAMENTO").assertIsDisplayed()
        composeRule.onNodeWithText("✂️").assertIsDisplayed()
        composeRule.onNodeWithText("Sexta, 30 Out • 14:00 • Camila").assertIsDisplayed()
        composeRule.onNodeWithText("Ver").performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun professionalCard_showsRatingAndAvailability() {
        composeRule.setContent { GlowbookTheme { ProfessionalCard(professional = camila, onClick = {}) } }

        composeRule.onNodeWithText("Camila Rocha").assertIsDisplayed()
        composeRule.onNodeWithText("Cabeleireira").assertIsDisplayed()
        composeRule.onNodeWithText("★★★★★ 4.9").assertIsDisplayed()
        composeRule.onNodeWithText("Hoje livre").assertIsDisplayed()
    }

    @Test
    fun professionalCard_showsUnavailableAndPartialStars() {
        composeRule.setContent { GlowbookTheme { ProfessionalCard(professional = bea, onClick = {}) } }

        composeRule.onNodeWithText("★★★☆☆ 3.0").assertIsDisplayed()
        composeRule.onNodeWithText("Indisponível").assertIsDisplayed()
    }

    @Test
    fun categoriesRow_reportsClickedIndex() {
        val clicked = mutableListOf<Int>()
        composeRule.setContent {
            GlowbookTheme {
                val selected = remember { mutableIntStateOf(0) }
                CategoriesRow(
                    categories = categories,
                    selectedIndex = selected.intValue,
                    onCategoryClick = { clicked += it; selected.intValue = it }
                )
            }
        }

        composeRule.onNodeWithText("Unhas").performClick()
        composeRule.onNodeWithText("Cabelo").performClick()

        assertEquals(listOf(1, 0), clicked)
    }

    @Test
    fun categoriesRow_scrollsWhenThereAreMoreThanFour() {
        val many = listOf("Cabelo", "Unhas", "Estética", "Spa", "Maquiagem", "Depilação").map { CategoryUi("💖", it) }
        composeRule.setContent {
            GlowbookTheme { CategoriesRow(categories = many, selectedIndex = 0, onCategoryClick = {}) }
        }

        composeRule.onNodeWithText("Cabelo").assertIsDisplayed()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Depilação"))
        composeRule.onNodeWithText("Depilação").assertIsDisplayed()
    }
}
