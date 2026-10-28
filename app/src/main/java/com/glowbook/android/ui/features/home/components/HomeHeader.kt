package com.glowbook.android.ui.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowbook.android.R
import com.glowbook.android.ui.common.theme.GlowHeaderEnd
import com.glowbook.android.ui.common.theme.GlowHeaderStart
import com.glowbook.android.ui.features.home.model.NextAppointmentUi

private val HeaderHorizontalPadding = 22.dp
private val AppointmentSpacing = 12.dp
private const val SCROLLING_CARD_WIDTH_FRACTION = 0.88f

@Composable
fun HomeHeader(
    userName: String,
    nextAppointments: List<NextAppointmentUi>,
    onNextAppointmentClick: (NextAppointmentUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(GlowHeaderStart, GlowHeaderEnd),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(top = 20.dp, bottom = 28.dp)
    ) {
        Text(
            text = stringResource(R.string.home_greeting, userName),
            modifier = Modifier.padding(horizontal = HeaderHorizontalPadding),
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.home_welcome_message),
            modifier = Modifier.padding(horizontal = HeaderHorizontalPadding),
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        if (nextAppointments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(18.dp))
            NextAppointmentsRow(
                appointments = nextAppointments,
                onAppointmentClick = onNextAppointmentClick
            )
        }
    }
}

@Composable
private fun NextAppointmentsRow(
    appointments: List<NextAppointmentUi>,
    onAppointmentClick: (NextAppointmentUi) -> Unit
) {
    if (appointments.size == 1) {
        val appointment = appointments.first()
        NextAppointmentCard(
            appointment = appointment,
            onClick = { onAppointmentClick(appointment) },
            modifier = Modifier.padding(horizontal = HeaderHorizontalPadding)
        )
    } else {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cardWidth = (maxWidth - HeaderHorizontalPadding * 2) * SCROLLING_CARD_WIDTH_FRACTION

            LazyRow(
                contentPadding = PaddingValues(horizontal = HeaderHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(AppointmentSpacing)
            ) {
                items(appointments) { appointment ->
                    NextAppointmentCard(
                        appointment = appointment,
                        onClick = { onAppointmentClick(appointment) },
                        modifier = Modifier.width(cardWidth)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeHeaderPreview() {
    HomeHeader(
        userName = "Ana Maria",
        nextAppointments = listOf(
            NextAppointmentUi(icon = "✂️", title = "Corte + Escova", subtitle = "Sexta, 27 Jun • 14:00 • Camila")
        ),
        onNextAppointmentClick = {}
    )
}

@Preview
@Composable
private fun HomeHeaderScrollablePreview() {
    HomeHeader(
        userName = "Ana Maria",
        nextAppointments = listOf(
            NextAppointmentUi(icon = "✨", title = "Design de sobrancelha", subtitle = "Domingo, 8 Nov • 15:00 • Fernanda"),
            NextAppointmentUi(icon = "💅", title = "Nail Art completo", subtitle = "Sexta, 13 Nov • 14:25 • Beatriz"),
            NextAppointmentUi(icon = "✂️", title = "Corte feminino + Coloração raiz", subtitle = "Sábado, 19 Dez • 10:00 • Camila")
        ),
        onNextAppointmentClick = {}
    )
}
