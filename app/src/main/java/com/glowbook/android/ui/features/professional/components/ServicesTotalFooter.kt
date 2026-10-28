package com.glowbook.android.ui.features.professional.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowRose
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.professional.model.ServicesSummaryUi

@Composable
fun ServicesTotalFooter(
    summary: ServicesSummaryUi,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().background(Color.White)) {
        HorizontalDivider(thickness = 1.dp, color = GlowBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = summary.label, fontSize = 12.sp, color = GlowMuted)
                Text(
                    text = summary.total,
                    modifier = Modifier.padding(top = 2.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GlowText
                )
            }
            ContinueButton(enabled = summary.count > 0, onClick = onContinueClick)
        }
    }
}

@Composable
private fun ContinueButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = "Escolher horário →",
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) GlowRose else GlowBorder)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 13.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = if (enabled) Color.White else GlowMuted
    )
}

@Preview
@Composable
private fun ServicesTotalFooterPreview() {
    ServicesTotalFooter(
        summary = ServicesSummaryUi(2, "2 serviços • 2h 15min", "R$ 260,00"),
        onContinueClick = {}
    )
}
