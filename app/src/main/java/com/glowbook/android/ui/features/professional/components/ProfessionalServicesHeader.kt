package com.glowbook.android.ui.features.professional.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.glowbook.android.R
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.appointments.components.BackButton
import com.glowbook.android.ui.features.home.model.ProfessionalUi
import java.util.Locale

@Composable
fun ProfessionalServicesHeader(
    professional: ProfessionalUi?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().background(Color.White)) {
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BackButton(onClick = onBackClick)
            if (professional != null) {
                ProfessionalMiniProfile(professional = professional)
            }
        }
        HorizontalDivider(thickness = 1.dp, color = GlowBorder)
    }
}

@Composable
private fun ProfessionalMiniProfile(professional: ProfessionalUi) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(professional.avatarBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(text = professional.avatar, fontSize = 18.sp)
        }
        Column {
            Text(
                text = professional.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GlowText
            )
            Text(
                text = "★ ${String.format(Locale.US, "%.1f", professional.rating)} • ${professional.specialty}",
                fontSize = 11.sp,
                color = GlowMuted
            )
        }
    }
}

@Preview
@Composable
private fun ProfessionalServicesHeaderPreview() {
    ProfessionalServicesHeader(
        professional = ProfessionalUi(1, "👩", Color(0xFFFDEBD0), "Camila Rocha", "Coloração", 4.9, R.string.professional_available_today),
        onBackClick = {}
    )
}
