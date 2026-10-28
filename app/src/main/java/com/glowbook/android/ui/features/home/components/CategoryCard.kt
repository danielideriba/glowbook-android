package com.glowbook.android.ui.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowRose
import com.glowbook.android.ui.common.theme.GlowRoseDark
import com.glowbook.android.ui.common.theme.GlowRoseLight
import com.glowbook.android.ui.common.theme.GlowText
import com.glowbook.android.ui.features.home.model.CategoryUi

private const val MAX_SHOWN_CATEGORIES = 4
private const val VISIBLE_CATEGORIES_WHEN_SCROLLING = 4.4f
private val CategorySpacing = 10.dp

@Composable
fun CategoryCard(
    category: CategoryUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = shape, ambientColor = Color.Black.copy(alpha = 0.04f))
            .clip(shape)
            .background(if (selected) GlowRoseLight else Color.White)
            .border(1.dp, if (selected) GlowRose else GlowBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = category.icon, fontSize = 26.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) GlowRoseDark else GlowText,
            maxLines = 1
        )
    }
}

@Composable
fun CategoriesRow(
    categories: List<CategoryUi>,
    selectedIndex: Int,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.size <= MAX_SHOWN_CATEGORIES) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CategorySpacing)
        ) {
            categories.forEachIndexed { index, category ->
                CategoryCard(
                    category = category,
                    selected = index == selectedIndex,
                    onClick = { onCategoryClick(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    } else {
        BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
            val cardWidth = (maxWidth - CategorySpacing * MAX_SHOWN_CATEGORIES) / VISIBLE_CATEGORIES_WHEN_SCROLLING

            LazyRow(horizontalArrangement = Arrangement.spacedBy(CategorySpacing)) {
                itemsIndexed(categories) { index, category ->
                    CategoryCard(
                        category = category,
                        selected = index == selectedIndex,
                        onClick = { onCategoryClick(index) },
                        modifier = Modifier.width(cardWidth)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriesRowPreview() {
    CategoriesRow(
        categories = listOf(CategoryUi("✂️", "Cabelo"), CategoryUi("💅", "Unhas"), CategoryUi("✨", "Estética"), CategoryUi("🧖", "Spa")),
        selectedIndex = 0,
        onCategoryClick = {},
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoriesRowScrollablePreview() {
    CategoriesRow(
        categories = listOf(
            CategoryUi("✂️", "Cabelo"),
            CategoryUi("💅", "Unhas"),
            CategoryUi("✨", "Estética"),
            CategoryUi("🧖", "Spa"),
            CategoryUi("💄", "Maquiagem"),
            CategoryUi("💖", "Depilação")
        ),
        selectedIndex = 0,
        onCategoryClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
