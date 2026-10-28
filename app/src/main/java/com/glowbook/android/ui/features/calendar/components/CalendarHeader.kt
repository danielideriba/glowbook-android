package com.glowbook.android.ui.features.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowbook.android.R
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.appointments.components.BackButton
import com.glowbook.android.ui.features.calendar.model.CalendarDayUi
import java.time.LocalDate

@Composable
fun CalendarHeader(
    days: List<CalendarDayUi>,
    selectedDate: LocalDate,
    onDayClick: (CalendarDayUi) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().background(Color.White)) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BackButton(onClick = onBackClick)
                Text(
                    text = stringResource(R.string.calendar_header_text),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlowText
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            CalendarDayStrip(
                days = days,
                selectedDate = selectedDate,
                onDayClick = onDayClick
            )
        }
        HorizontalDivider(thickness = 1.dp, color = GlowBorder)
    }
}

@Composable
fun CalendarDayStrip(
    days: List<CalendarDayUi>,
    selectedDate: LocalDate,
    onDayClick: (CalendarDayUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEach { day ->
            CalendarDayItem(
                day = day,
                selected = day.date == selectedDate,
                onClick = { onDayClick(day) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
private fun CalendarHeaderPreview() {
    val today = LocalDate.of(2026, 6, 23)
    CalendarHeader(
        days = (0L..5L).map { i ->
            val date = today.plusDays(i)
            CalendarDayUi(date, listOf("SEG", "TER", "QUA", "QUI", "SEX", "SÁB")[i.toInt()], date.dayOfMonth.toString(), i % 2 == 0L)
        },
        selectedDate = today.plusDays(3),
        onDayClick = {},
        onBackClick = {}
    )
}
