package com.glowbook.android.ui.features.professional.components

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
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowRose
import com.glowbook.android.ui.common.theme.GlowRoseDark
import com.glowbook.android.ui.common.theme.GlowRoseLight
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.professional.model.ServiceUi

@Composable
fun ServiceItemCard(
    service: ServiceUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.dp, if (selected) GlowRose else GlowBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GlowRoseLight),
            contentAlignment = Alignment.Center
        ) {
            Text(text = service.icon, fontSize = 20.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = service.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GlowText
            )
            Text(
                text = service.duration,
                modifier = Modifier.padding(top = 2.dp),
                fontSize = 11.sp,
                color = GlowMuted
            )
        }

        Text(
            text = service.price,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GlowRoseDark
        )

        ServiceSelectionBox(selected = selected)
    }
}

@Composable
fun ServiceSelectionBox(
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(shape)
            .background(if (selected) GlowRose else Color.Transparent)
            .border(2.dp, if (selected) GlowRose else GlowBorder, shape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Text(text = "✓", fontSize = 14.sp, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceItemCardPreview() {
    ServiceItemCard(
        service = ServiceUi(1, "✂️", "Corte feminino", "⏱ 45 min", "R$ 80", 80.0, 45),
        selected = true,
        onClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
