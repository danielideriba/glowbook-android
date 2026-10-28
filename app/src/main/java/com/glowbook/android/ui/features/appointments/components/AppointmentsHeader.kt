package com.glowbook.android.ui.features.appointments.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowCream
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.appointments.model.AppointmentsTab

@Composable
fun AppointmentsHeader(
    selectedTab: AppointmentsTab,
    onTabClick: (AppointmentsTab) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().background(Color.White)) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BackButton(onClick = onBackClick)
                Text(
                    text = "Meus agendamentos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlowText
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            AppointmentsTabs(selectedTab = selectedTab, onTabClick = onTabClick)
        }
        HorizontalDivider(thickness = 1.dp, color = GlowBorder)
    }
}

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(GlowCream)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "←", fontSize = 16.sp, color = GlowText)
    }
}

@Preview
@Composable
private fun AppointmentsHeaderPreview() {
    AppointmentsHeader(
        selectedTab = AppointmentsTab.Upcoming,
        onTabClick = {},
        onBackClick = {}
    )
}
