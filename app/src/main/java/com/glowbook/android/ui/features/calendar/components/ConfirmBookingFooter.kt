package com.glowbook.android.ui.features.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowbook.android.R
import com.glowbook.android.ui.common.theme.GlowBorder
import com.glowbook.android.ui.common.theme.GlowMuted
import com.glowbook.android.ui.common.theme.GlowRose

@Composable
fun ConfirmBookingFooter(
    enabled: Boolean,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false
) {
    val clickable = enabled && !loading
    Column(modifier = modifier.fillMaxWidth().background(Color.White)) {
        HorizontalDivider(thickness = 1.dp, color = GlowBorder)
        Text(
            text = stringResource(if (loading) R.string.confirm_loading_text_button else R.string.confirm_text_button),
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 18.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (clickable) GlowRose else GlowBorder)
                .clickable(enabled = clickable, onClick = onConfirmClick)
                .padding(15.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (clickable) Color.White else GlowMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun ConfirmBookingFooterPreview() {
    ConfirmBookingFooter(enabled = true, onConfirmClick = {})
}
