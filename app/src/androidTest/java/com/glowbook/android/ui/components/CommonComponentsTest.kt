package com.glowbook.android.ui.components

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.glowbook.android.ui.common.theme.GlowbookTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommonComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun sectionLabel_showsTextInUppercase() {
        composeRule.setContent { GlowbookTheme { SectionLabel(text = "Profissionais disponíveis") } }

        composeRule.onNodeWithText("PROFISSIONAIS DISPONÍVEIS").assertIsDisplayed()
        composeRule.onAllNodesWithText("Profissionais disponíveis").assertCountEquals(0)
    }

    @Test
    fun bottomNav_showsAllItems() {
        composeRule.setContent { GlowbookTheme { GlowBottomNavBar(selected = BottomNavItem.Home, onItemClick = {}) } }

        listOf("Início", "Buscar", "Agenda", "Perfil").forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
        listOf("🏠", "🔍", "📅", "👤").forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun bottomNav_reportsClickedItem() {
        val clicked = mutableListOf<BottomNavItem>()
        composeRule.setContent {
            GlowbookTheme { GlowBottomNavBar(selected = BottomNavItem.Home, onItemClick = { clicked += it }) }
        }

        composeRule.onNodeWithText("Agenda").performClick()
        composeRule.onNodeWithText("Perfil").performClick()
        composeRule.onNodeWithText("Início").performClick()

        assertEquals(listOf(BottomNavItem.Schedule, BottomNavItem.Profile, BottomNavItem.Home), clicked)
    }
}
