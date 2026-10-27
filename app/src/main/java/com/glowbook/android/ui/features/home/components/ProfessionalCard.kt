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
import com.glowbook.android.ui.common.theme.GlowAvailableBg
import com.glowbook.android.ui.common.theme.GlowAvailableText
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowGold
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.home.model.HomeSampleData
import com.glowbook.android.ui.features.home.model.ProfessionalUi
import java.util.Locale

@Composable
fun ProfessionalCard(
    professional: ProfessionalUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.dp, GlowBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(professional.avatarBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(text = professional.avatar, fontSize = 22.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = professional.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GlowText
            )
            Text(
                text = professional.specialty,
                fontSize = 11.sp,
                color = GlowMuted
            )
            Text(
                text = "${starsFor(professional.rating)} ${String.format(Locale.US, "%.1f", professional.rating)}",
                modifier = Modifier.padding(top = 3.dp),
                fontSize = 11.sp,
                color = GlowGold
            )
        }

        Text(
            text = professional.availability,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(GlowAvailableBg)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GlowAvailableText
        )
    }
}

private fun starsFor(rating: Double): String {
    // 4.9 → ★★★★★, 4.7 → ★★★★☆ (same as the mockup)
    val filled = (1..5).count { rating >= it - 0.15 }
    return "★".repeat(filled) + "☆".repeat(5 - filled)
}

@Preview(showBackground = true)
@Composable
private fun ProfessionalCardPreview() {
    ProfessionalCard(
        professional = HomeSampleData.professionals.first(),
        onClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
