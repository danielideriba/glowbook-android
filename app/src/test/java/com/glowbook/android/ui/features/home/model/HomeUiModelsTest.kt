package com.glowbook.android.ui.features.home.model

import androidx.compose.ui.graphics.Color
import com.glowbook.android.R
import com.glowbook.android.domain.models.AppointmentEntity
import com.glowbook.android.domain.models.CategoryEntity
import com.glowbook.android.domain.models.ProfessionalEntity
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class HomeUiModelsTest {

    @Before
    fun setUp() {
        // android.graphics.Color is only a stub on the JVM.
        mockkStatic(android.graphics.Color::class)
        every { android.graphics.Color.parseColor("#FDE8EC") } returns 0xFFFDE8EC.toInt()
        every { android.graphics.Color.parseColor("invalid") } throws IllegalArgumentException("Unknown color")
    }

    @After
    fun tearDown() {
        unmockkStatic(android.graphics.Color::class)
    }

    private fun professional(avatar: String? = "🧑", color: String? = "#FDE8EC", available: Boolean = true) =
        ProfessionalEntity(1, "Camila Rocha", "Cabeleireira", 4.9, avatar, color, available, 1)

    private fun appointment(category: String = "Cabelo") = AppointmentEntity(
        id = 5,
        services = "Corte + Escova",
        price = 260.0,
        durationMinutes = 135,
        date = LocalDate.of(2026, 10, 30),
        time = LocalTime.of(14, 0),
        status = "confirmado",
        professionalId = 1,
        professionalName = "Camila Rocha",
        professionalAvatar = null,
        professionalRating = 4.9,
        category = category
    )

    @Test
    fun `maps category to icon ignoring case`() {
        assertEquals(CategoryUi("✂️", "Cabelo"), CategoryEntity(1, "Cabelo").toUi())
        assertEquals("💅", CategoryEntity(2, "UNHAS").toUi().icon)
        assertEquals("✨", CategoryEntity(3, "Estética").toUi().icon)
        assertEquals("🧖", CategoryEntity(4, "Spa").toUi().icon)
        assertEquals("🦶", CategoryEntity(5, "Pedicure").toUi().icon)
        assertEquals("💖", CategoryEntity(6, "Maquiagem").toUi().icon)
    }

    @Test
    fun `maps next appointment with full weekday and first name`() {
        assertEquals(
            NextAppointmentUi(
                icon = "✂️",
                title = "Corte + Escova",
                subtitle = "Sexta, 30 Out • 14:00 • Camila"
            ),
            appointment().toUi()
        )
    }

    @Test
    fun `next appointment icon follows category`() {
        assertEquals("💅", appointment(category = "Unhas").toUi().icon)
    }

    @Test
    fun `maps professional to ui`() {
        assertEquals(
            ProfessionalUi(
                id = 1,
                avatar = "🧑",
                avatarBackground = Color(0xFFFDE8EC),
                name = "Camila Rocha",
                specialty = "Cabeleireira",
                rating = 4.9,
                availability = R.string.professional_available_today
            ),
            professional().toUi()
        )
    }

    @Test
    fun `professional uses defaults for missing avatar and color`() {
        val ui = professional(avatar = null, color = null).toUi()

        assertEquals("👩", ui.avatar)
        assertEquals(Color(0xFFFDEBD0), ui.avatarBackground)
    }

    @Test
    fun `professional falls back to default color when color is invalid`() {
        assertEquals(Color(0xFFFDEBD0), professional(color = "invalid").toUi().avatarBackground)
    }

    @Test
    fun `unavailable professional uses unavailable label`() {
        assertEquals(R.string.professional_unavailable, professional(available = false).toUi().availability)
    }
}
