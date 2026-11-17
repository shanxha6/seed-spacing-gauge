package com.seedspacing.gauge.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.seedspacing.gauge.core.theme.LocalSeedColors
import com.seedspacing.gauge.domain.model.FillVerdict

@Composable
fun FillGauge(
    percent: Int,
    verdict: FillVerdict,
    verdictText: String,
    percentText: String,
    modifier: Modifier = Modifier,
    dimmed: Boolean = false
) {
    val colors = LocalSeedColors.current
    val target: Color = when (verdict) {
        FillVerdict.ROOM_TO_SPARE -> colors.accent
        FillVerdict.BALANCED -> colors.primary
        FillVerdict.PACKED_TIGHT -> colors.danger
    }
    val barColor by animateColorAsState(targetValue = target, animationSpec = tween(280), label = "gaugeColor")
    val fraction by animateFloatAsState(
        targetValue = (percent / 100f).coerceIn(0f, 1f),
        animationSpec = tween(280),
        label = "gaugeFill"
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = verdictText,
                style = MaterialTheme.typography.labelSmall,
                color = barColor,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            Text(
                text = percentText,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(colors.secondary.copy(alpha = 0.35f), RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(
                        barColor.copy(alpha = if (dimmed) 0.55f else 1f),
                        RoundedCornerShape(5.dp)
                    )
            )
        }
    }
}
