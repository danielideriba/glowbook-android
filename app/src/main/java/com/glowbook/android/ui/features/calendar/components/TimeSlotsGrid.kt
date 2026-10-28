package com.glowbook.android.ui.features.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowRose
import com.glowbook.android.ui.common.theme.GlowSlotTakenBg
import com.glowbook.android.ui.common.theme.GlowSlotTakenBorder
import com.glowbook.android.ui.common.theme.GlowSlotTakenText
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.calendar.model.TimeSlotUi
import java.time.LocalTime

private const val SLOTS_PER_ROW = 3

@Composable
fun TimeSlotsGrid(
    slots: List<TimeSlotUi>,
    selectedTime: LocalTime?,
    onSlotClick: (TimeSlotUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        slots.chunked(SLOTS_PER_ROW).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { slot ->
                    TimeSlotItem(
                        slot = slot,
                        selected = slot.time == selectedTime,
                        onClick = { onSlotClick(slot) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(SLOTS_PER_ROW - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
fun TimeSlotItem(
    slot: TimeSlotUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val (background, border, textColor) = when {
        slot.taken -> Triple(GlowSlotTakenBg, GlowSlotTakenBorder, GlowSlotTakenText)
        selected -> Triple(GlowRose, GlowRose, Color.White)
        else -> Triple(Color.White, GlowBorder, GlowText)
    }

    Text(
        text = slot.label,
        modifier = modifier
            .clip(shape)
            .background(background)
            .border(1.5.dp, border, shape)
            .clickable(enabled = !slot.taken, onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = textColor,
        textAlign = TextAlign.Center,
        textDecoration = if (slot.taken) TextDecoration.LineThrough else null
    )
}

@Preview(showBackground = true)
@Composable
private fun TimeSlotsGridPreview() {
    TimeSlotsGrid(
        slots = listOf(8, 9, 10, 11, 14, 15, 16, 17, 18).map {
            TimeSlotUi(LocalTime.of(it, 0), "%02d:00".format(it), taken = it == 8 || it == 10 || it == 16)
        },
        selectedTime = LocalTime.of(14, 0),
        onSlotClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
