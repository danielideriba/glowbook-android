package com.glowbook.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowRose

enum class BottomNavItem(val icon: String, val label: String) {
    Home("🏠", "Início"),
    Search("🔍", "Buscar"),
    Schedule("📅", "Agenda"),
    Profile("👤", "Perfil")
}

@Composable
fun GlowBottomNavBar(
    selected: BottomNavItem,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().background(Color.White)) {
        HorizontalDivider(thickness = 1.dp, color = GlowBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem.entries.forEach { item ->
                GlowBottomNavItem(
                    item = item,
                    selected = item == selected,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun GlowBottomNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) GlowRose else GlowMuted
    Column(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(text = item.icon, fontSize = 20.sp)
        Text(text = item.label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = color)
        if (selected) {
            Box(modifier = Modifier.size(5.dp).background(GlowRose, CircleShape))
        } else {
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Preview
@Composable
private fun GlowBottomNavBarPreview() {
    GlowBottomNavBar(selected = BottomNavItem.Home, onItemClick = {})
}
