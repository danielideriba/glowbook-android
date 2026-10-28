package com.glowbook.android.ui.features.home.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.glowbook.android.R
import com.glowbook.android.domain.models.AppointmentEntity
import com.glowbook.android.domain.models.CategoryEntity
import com.glowbook.android.domain.models.ProfessionalEntity
import java.time.format.TextStyle
import com.glowbook.android.utils.LocaleConstants.PT_BR

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
    val id: Long,
    val avatar: String,
    val avatarBackground: Color,
    val name: String,
    val specialty: String,
    val rating: Double,
    @StringRes val availability: Int
)

private fun categoryIcon(categoryName: String): String = when (categoryName.lowercase(PT_BR)) {
    "cabelo" -> "✂️"
    "unhas" -> "💅"
    "estética" -> "✨"
    "spa" -> "🧖"
    "pedicure" -> "🦶"
    else -> "💖"
}

fun CategoryEntity.toUi() = CategoryUi(
    icon = categoryIcon(name),
    label = name
)

fun AppointmentEntity.toUi(): NextAppointmentUi {
    val weekDay = date.dayOfWeek.getDisplayName(TextStyle.FULL, PT_BR)
        .substringBefore("-")
        .replaceFirstChar { it.titlecase(PT_BR) }
    val month = date.month.getDisplayName(TextStyle.SHORT, PT_BR)
        .removeSuffix(".")
        .replaceFirstChar { it.titlecase(PT_BR) }
    val hour = "%02d:%02d".format(time.hour, time.minute)
    val professionalFirstName = professionalName.substringBefore(" ")

    return NextAppointmentUi(
        icon = categoryIcon(category),
        title = services,
        subtitle = "$weekDay, ${date.dayOfMonth} $month • $hour • $professionalFirstName"
    )
}

fun ProfessionalEntity.toUi() = ProfessionalUi(
    id = id,
    avatar = avatar ?: "👩",
    avatarBackground = avatarColor
        ?.let { runCatching { Color(it.toColorInt()) }.getOrNull() }
        ?: Color(0xFFFDEBD0),
    name = name,
    specialty = specialty,
    rating = rating,
    availability = if (available) R.string.professional_available_today else R.string.professional_unavailable
)
