package com.glowbook.android.ui.features.appointments.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowbook.android.R
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowDanger
import com.glowbook.android.ui.common.theme.GlowGold
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowRose
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.appointments.model.AppointmentStatusUi
import com.glowbook.android.ui.features.appointments.model.AppointmentUi
import com.glowbook.android.ui.features.appointments.model.AppointmentsTab

@Composable
fun AppointmentCard(
    appointment: AppointmentUi,
    onClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onRescheduleClick: () -> Unit,
    onCancelClick: () -> Unit,
    onRateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.dp, GlowBorder, shape)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(appointment.status.stripe)
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.services,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlowText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = appointment.professional,
                        modifier = Modifier.padding(top = 2.dp),
                        fontSize = 11.sp,
                        color = GlowMuted
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                AppointmentStatusBadge(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            AppointmentMeta(appointment = appointment)
        }

        HorizontalDivider(thickness = 1.dp, color = GlowBorder)
        AppointmentActions(
            tab = appointment.tab,
            onDetailsClick = onDetailsClick,
            onRescheduleClick = onRescheduleClick,
            onCancelClick = onCancelClick,
            onRateClick = onRateClick
        )
    }
}

@Composable
fun AppointmentStatusBadge(
    status: AppointmentStatusUi,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(status.label),
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(status.badgeBackground)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = status.badgeText
    )
}

@Composable
private fun AppointmentMeta(appointment: AppointmentUi) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        AppointmentMetaItem(icon = "📅", text = appointment.date)
        AppointmentMetaItem(icon = "🕑", text = appointment.time)
        AppointmentMetaItem(icon = "💰", text = appointment.price)
    }
}

@Composable
private fun AppointmentMetaItem(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = icon, fontSize = 11.sp)
        Text(text = text, fontSize = 11.sp, color = GlowMuted)
    }
}

@Composable
private fun AppointmentActions(
    tab: AppointmentsTab,
    onDetailsClick: () -> Unit,
    onRescheduleClick: () -> Unit,
    onCancelClick: () -> Unit,
    onRateClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AppointmentAction(text = stringResource(R.string.appointment_action_details), onClick = onDetailsClick)
        when (tab) {
            AppointmentsTab.Upcoming -> {
                AppointmentAction(text = stringResource(R.string.appointment_action_reschedule), onClick = onRescheduleClick)
                AppointmentAction(text = stringResource(R.string.appointment_action_cancel), color = GlowDanger, onClick = onCancelClick)
            }

            AppointmentsTab.History -> {
                AppointmentAction(text = stringResource(R.string.appointment_action_rate), color = GlowGold, onClick = onRateClick)
                AppointmentAction(text = stringResource(R.string.appointment_action_reschedule), onClick = onRescheduleClick)
            }

            AppointmentsTab.Cancelled -> {
                AppointmentAction(text = stringResource(R.string.appointment_action_reschedule), onClick = onRescheduleClick)
            }
        }
    }
}

@Composable
private fun AppointmentAction(
    text: String,
    onClick: () -> Unit,
    color: Color = GlowRose
) {
    Text(
        text = text,
        modifier = Modifier.clickable(onClick = onClick),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = color
    )
}

@Preview(showBackground = true)
@Composable
private fun AppointmentCardPreview() {
    AppointmentCard(
        appointment = AppointmentUi(
            id = 1,
            services = "Corte + Escova",
            professional = "👩 Camila Rocha",
            status = AppointmentStatusUi.Confirmed,
            date = "Qui, 26 Jun",
            time = "14:00",
            price = "R$ 260",
            tab = AppointmentsTab.Upcoming
        ),
        onClick = {},
        onDetailsClick = {},
        onRescheduleClick = {},
        onCancelClick = {},
        onRateClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
