package com.glowbook.android.ui.features.home.model

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.glowbook.android.domain.entity.ProfessionalEntity

data class NextAppointmentUi(
    val icon: String,
    val title: String,
    val subtitle: String
)

data class CategoryUi(
    val icon: String,
    val label: String
)

data class ProfessionalUi(
    val avatar: String,
    val avatarBackground: Color,
    val name: String,
    val specialty: String,
    val rating: Double,
    val availability: String
)

fun ProfessionalEntity.toUi() = ProfessionalUi(
    avatar = avatar ?: "👩",
    avatarBackground = avatarColor
        ?.let { runCatching { Color(it.toColorInt()) }.getOrNull() }
        ?: Color(0xFFFDEBD0),
    name = name,
    specialty = specialty,
    rating = rating,
    availability = if (available) "Hoje livre" else "Indisponível"
)

// Mock data until the API is wired in
object HomeSampleData {
    val nextAppointment = NextAppointmentUi(
        icon = "✂️",
        title = "Corte + Escova",
        subtitle = "Sexta, 27 Jun • 14:00 • Camila"
    )

    val categories = listOf(
        CategoryUi("✂️", "Cabelo"),
        CategoryUi("💅", "Unhas"),
        CategoryUi("✨", "Estética"),
        CategoryUi("🧖", "Spa")
    )

    val professionals = listOf(
        ProfessionalUi("👩", Color(0xFFFDEBD0), "Camila Rocha", "Cabelo • Coloração", 4.9, "Hoje livre"),
        ProfessionalUi("🧑", Color(0xFFE8D5F5), "Beatriz Lima", "Unhas • Nail Art", 4.7, "Amanhã"),
        ProfessionalUi("👩", Color(0xFFD5EAF5), "Fernanda Costa", "Estética • Sobrancelha", 5.0, "Hoje livre")
    )
}
