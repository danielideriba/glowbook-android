package com.glowbook.android.ui.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowbook.android.ui.common.theme.GlowHeaderStart
import com.glowbook.android.ui.common.theme.GlowRose
import com.glowbook.android.ui.common.theme.GlowRoseDark
import com.glowbook.android.ui.features.home.model.HomeSampleData
import com.glowbook.android.ui.features.home.model.NextAppointmentUi

@Composable
fun NextAppointmentCard(
    appointment: NextAppointmentUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GlowRose),
            contentAlignment = Alignment.Center
        ) {
            Text(text = appointment.icon, fontSize = 20.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "PRÓXIMO AGENDAMENTO",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.65f)
            )
            Text(
                text = appointment.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = appointment.subtitle,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }

        Text(
            text = "Ver",
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 7.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GlowRoseDark
        )
    }
}

@Preview
@Composable
private fun NextAppointmentCardPreview() {
    Box(modifier = Modifier.background(GlowHeaderStart).padding(16.dp)) {
        NextAppointmentCard(appointment = HomeSampleData.nextAppointment, onClick = {})
    }
}
