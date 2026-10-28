package com.glowbook.android.ui.features.calendar.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowbook.android.R
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowRoseLight
import com.glowbook.android.ui.common.theme.GlowSummaryBorder
import com.glowbook.android.ui.common.theme.GlowSummaryDivider
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.calendar.model.BookingSummaryUi

@Composable
fun BookingSummaryCard(
    summary: BookingSummaryUi,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(GlowRoseLight)
            .border(1.dp, GlowSummaryBorder, shape)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        BookingSummaryRow(label = stringResource(R.string.booking_row_text_professional), value = summary.professional)
        DashedDivider()
        BookingSummaryRow(label = stringResource(R.string.booking_row_text_services), value = summary.services)
        DashedDivider()
        BookingSummaryRow(label = stringResource(R.string.booking_row_text_date_hour), value = summary.dateTime)
        DashedDivider()
        BookingSummaryRow(label = stringResource(R.string.booking_row_text_estimate_time), value = summary.duration)
    }
}

@Composable
private fun BookingSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = GlowMuted)
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = GlowText,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .height(1.dp)
    ) {
        drawLine(
            color = GlowSummaryDivider,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = size.height,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookingSummaryCardPreview() {
    BookingSummaryCard(
        summary = BookingSummaryUi("Camila Rocha", "Corte + Escova", "Qui, 26 Jun • 14:00", "~2h 15min"),
        modifier = Modifier.padding(16.dp)
    )
}
