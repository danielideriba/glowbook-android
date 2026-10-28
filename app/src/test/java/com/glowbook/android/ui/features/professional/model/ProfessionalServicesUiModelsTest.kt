package com.glowbook.android.ui.features.professional.model

import com.glowbook.android.domain.models.ServiceEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfessionalServicesUiModelsTest {

    private fun service(id: Long, price: Double, minutes: Int, icon: String? = null) =
        ServiceEntity(id, "Serviço $id", price, minutes, icon, 1)

    private val services = listOf(
        service(1, 120.0, 45).toUi(),
        service(2, 140.0, 90).toUi(),
        service(3, 500.5, 30).toUi()
    )

    @Test
    fun `maps service to ui`() {
        assertEquals(
            ServiceUi(
                id = 1,
                icon = "✂️",
                name = "Serviço 1",
                duration = "⏱ 45 min",
                price = "R$ 120",
                priceValue = 120.0,
                durationMinutes = 45
            ),
            service(1, 120.0, 45, "scissors").toUi()
        )
    }

    @Test
    fun `formats price with cents in pt-BR`() {
        assertEquals("R$ 120,50", service(1, 120.5, 45).toUi().price)
    }

    @Test
    fun `maps icon names to emojis`() {
        val icons = listOf("scissors", "hair-wind", "color-palette", "face-massage", "nail-art-complete", "eyebrow", "other", null)
            .map { service(1, 1.0, 1, it).toUi().icon }

        assertEquals(listOf("✂️", "💨", "🎨", "💆", "💅", "👁️", "✨", "✨"), icons)
    }

    @Test
    fun `summary for several selected services`() {
        assertEquals(
            ServicesSummaryUi(count = 2, label = "2 serviços • 2h 15min", total = "R$ 260,00"),
            services.summaryFor(setOf(1, 2))
        )
    }

    @Test
    fun `summary for one service uses singular`() {
        assertEquals(
            ServicesSummaryUi(count = 1, label = "1 serviço • 45min", total = "R$ 120,00"),
            services.summaryFor(setOf(1))
        )
    }

    @Test
    fun `summary for whole hours`() {
        assertEquals("1 serviço • 1h 30min", services.summaryFor(setOf(2)).label)
        assertEquals("2 serviços • 2h", services.summaryFor(setOf(2, 3)).label)
    }

    @Test
    fun `summary with nothing selected`() {
        assertEquals(
            ServicesSummaryUi(count = 0, label = "0 serviços • 0min", total = "R$ 0,00"),
            services.summaryFor(emptySet())
        )
    }

    @Test
    fun `summary ignores ids not in the list`() {
        assertEquals(1, services.summaryFor(setOf(1, 99)).count)
    }

    @Test
    fun `summary total uses thousands separator`() {
        val expensive = listOf(service(1, 1250.75, 60).toUi())

        assertEquals("R$ 1.250,75", expensive.summaryFor(setOf(1)).total)
    }
}
