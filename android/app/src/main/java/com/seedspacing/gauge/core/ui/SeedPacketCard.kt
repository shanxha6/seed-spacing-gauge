package com.seedspacing.gauge.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seedspacing.gauge.core.theme.LocalSeedColors

@Composable
fun SeedPacketCard(
    modifier: Modifier = Modifier,
    padding: Dp = 20.dp,
    elevation: Dp = 4.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalSeedColors.current
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = colors.primary.copy(alpha = 0.18f),
                spotColor = colors.accent.copy(alpha = 0.22f)
            )
            .background(colors.card, shape)
            .border(1.dp, colors.secondary.copy(alpha = 0.75f), shape)
            .padding(padding),
        content = content
    )
}
