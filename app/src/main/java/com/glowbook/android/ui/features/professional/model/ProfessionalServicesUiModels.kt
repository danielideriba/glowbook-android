package com.glowbook.android.ui.features.professional.model

import com.glowbook.android.domain.models.ServiceEntity
import java.text.NumberFormat
import com.glowbook.android.utils.LocaleConstants.PT_BR

data class ServiceUi(
    val id: Long,
    val icon: String,
    val name: String,
    val duration: String,
    val price: String,
    val priceValue: Double,
    val durationMinutes: Int
)

data class ServicesSummaryUi(
    val count: Int,
    val label: String,
    val total: String
)

private fun serviceIcon(icon: String?): String = when (icon) {
    "scissors" -> "✂️"
    "hair-wind" -> "💨"
    "color-palette" -> "🎨"
    "face-massage" -> "💆"
    "nail-art-complete" -> "💅"
    "eyebrow" -> "👁️"
    else -> "✨"
}

private fun Double.toPriceLabel(): String =
    if (this % 1.0 == 0.0) "R$ ${toLong()}" else "R$ ${"%.2f".format(PT_BR, this)}"

private fun Double.toTotalLabel(): String =
    NumberFormat.getCurrencyInstance(PT_BR).format(this).replace('\u00A0', ' ')

private fun Int.toDurationLabel(): String {
    val hours = this / 60
    val minutes = this % 60
    return when {
        hours == 0 -> "${minutes}min"
        minutes == 0 -> "${hours}h"
        else -> "${hours}h ${minutes}min"
    }
}

fun ServiceEntity.toUi() = ServiceUi(
    id = id,
    icon = serviceIcon(icon),
    name = name,
    duration = "⏱ $durationMinutes min",
    price = price.toPriceLabel(),
    priceValue = price,
    durationMinutes = durationMinutes
)

fun List<ServiceUi>.summaryFor(selectedIds: Set<Long>): ServicesSummaryUi {
    val selected = filter { it.id in selectedIds }
    val count = selected.size
    val minutes = selected.sumOf { it.durationMinutes }
    return ServicesSummaryUi(
        count = count,
        label = "$count serviço${if (count != 1) "s" else ""} • ${minutes.toDurationLabel()}",
        total = selected.sumOf { it.priceValue }.toTotalLabel()
    )
}
