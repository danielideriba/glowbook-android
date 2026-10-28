package com.glowbook.android.ui.features.appointments.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.glowbook.android.R
import com.glowbook.android.domain.models.AppointmentEntity
import com.glowbook.android.ui.common.theme.GlowAvailableBg
import com.glowbook.android.ui.common.theme.GlowAvailableText
import com.glowbook.android.ui.common.theme.GlowDanger
import com.glowbook.android.ui.common.theme.GlowDangerBg
import com.glowbook.android.ui.common.theme.GlowDoneBg
import com.glowbook.android.ui.common.theme.GlowDoneStripe
import com.glowbook.android.ui.common.theme.GlowDoneText
import com.glowbook.android.ui.common.theme.GlowGold
import com.glowbook.android.ui.common.theme.GlowPendingBg
import com.glowbook.android.ui.common.theme.GlowPendingText
import com.glowbook.android.ui.common.theme.GlowRose
import java.time.format.TextStyle
import com.glowbook.android.utils.LocaleConstants.PT_BR

enum class AppointmentsTab(@StringRes val label: Int) {
    Upcoming(R.string.tab_text_next),
    History(R.string.tab_text_history),
    Cancelled(R.string.tab_text_cancelled)
}

enum class AppointmentStatusUi(
    @StringRes val label: Int,
    val badgeBackground: Color,
    val badgeText: Color,
    val stripe: Color
) {
    Confirmed(R.string.status_confirmed, GlowAvailableBg, GlowAvailableText, GlowRose),
    Pending(R.string.status_pending, GlowPendingBg, GlowPendingText, GlowGold),
    Done(R.string.status_done, GlowDoneBg, GlowDoneText, GlowDoneStripe),
    Cancelled(R.string.status_cancelled, GlowDangerBg, GlowDanger, GlowDanger)
}

data class AppointmentUi(
    val id: Long,
    val services: String,
    val professional: String,
    val status: AppointmentStatusUi,
    val date: String,
    val time: String,
    val price: String,
    val tab: AppointmentsTab
)

private fun String.toStatusUi(): AppointmentStatusUi = when (lowercase(PT_BR)) {
    "confirmado" -> AppointmentStatusUi.Confirmed
    "concluido", "concluído" -> AppointmentStatusUi.Done
    "cancelado" -> AppointmentStatusUi.Cancelled
    else -> AppointmentStatusUi.Pending
}

private fun Double.toPriceLabel(): String =
    if (this % 1.0 == 0.0) "R$ ${toLong()}" else "R$ " + "%.2f".format(PT_BR, this)

fun AppointmentEntity.toUi(): AppointmentUi {
    val status = status.toStatusUi()
    val tab = when {
        status == AppointmentStatusUi.Cancelled -> AppointmentsTab.Cancelled
        status == AppointmentStatusUi.Done || isPast -> AppointmentsTab.History
        else -> AppointmentsTab.Upcoming
    }

    val weekDay = date.dayOfWeek.getDisplayName(TextStyle.SHORT, PT_BR)
        .removeSuffix(".")
        .replaceFirstChar { it.titlecase(PT_BR) }
    val month = date.month.getDisplayName(TextStyle.SHORT, PT_BR)
        .removeSuffix(".")
        .replaceFirstChar { it.titlecase(PT_BR) }

    return AppointmentUi(
        id = id,
        services = services,
        professional = "${professionalAvatar ?: "👩"} $professionalName",
        status = status,
        date = "$weekDay, ${date.dayOfMonth} $month",
        time = "%02d:%02d".format(time.hour, time.minute),
        price = (price ?: 0.0).toPriceLabel(),
        tab = tab
    )
}
