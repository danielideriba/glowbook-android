package com.glowbook.android.ui.features.professional.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun SelectAllServicesChip(
    allSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    Text(
        text = if (allSelected) "✓ Todos" else "Selecionar todos",
        modifier = modifier
            .clip(shape)
            .background(if (allSelected) GlowRose else Color.White)
            .border(1.5.dp, if (allSelected) GlowRose else GlowBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (allSelected) Color.White else GlowMuted
    )
}

@Preview(showBackground = true)
@Composable
private fun SelectAllServicesChipPreview() {
    SelectAllServicesChip(allSelected = false, onClick = {}, modifier = Modifier.padding(8.dp))
}
