package com.glowbook.android.ui.features.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowGold
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowRose
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.calendar.model.CalendarDayUi
import java.time.LocalDate

@Composable
fun CalendarDayItem(
    day: CalendarDayUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val dotColor = when {
        selected -> Color.White.copy(alpha = 0.6f)
        day.hasBookings -> GlowGold
        else -> Color.Transparent
    }

    Column(
        modifier = modifier
            .clip(shape)
            .background(if (selected) GlowRose else Color.White)
            .border(1.5.dp, if (selected) GlowRose else GlowBorder, shape)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.dayOfWeek,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else GlowMuted
        )
        Text(
            text = day.dayNumber,
            modifier = Modifier.padding(vertical = 3.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (selected) Color.White else GlowText
        )
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarDayItemPreview() {
    CalendarDayItem(
        day = CalendarDayUi(LocalDate.of(2026, 6, 26), "QUI", "26", true),
        selected = true,
        onClick = {},
        modifier = Modifier.padding(8.dp)
    )
}
