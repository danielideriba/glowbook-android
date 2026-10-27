package com.glowbook.android.ui.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowbook.android.ui.common.theme.GlowHeaderEnd
import com.glowbook.android.ui.common.theme.GlowHeaderStart
import com.glowbook.android.ui.features.home.model.HomeSampleData
import com.glowbook.android.ui.features.home.model.NextAppointmentUi

@Composable
fun HomeHeader(
    userName: String,
    nextAppointment: NextAppointmentUi?,
    onNextAppointmentClick: () -> Unit,
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
            .padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 28.dp)
    ) {
        Text(
            text = "Olá, $userName 👋",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "O que vamos fazer hoje?",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        if (nextAppointment != null) {
            Spacer(modifier = Modifier.height(18.dp))
            NextAppointmentCard(
                appointment = nextAppointment,
                onClick = onNextAppointmentClick
            )
        }
    }
}

@Preview
@Composable
private fun HomeHeaderPreview() {
    HomeHeader(
        userName = "Ana",
        nextAppointment = HomeSampleData.nextAppointment,
        onNextAppointmentClick = {}
    )
}
