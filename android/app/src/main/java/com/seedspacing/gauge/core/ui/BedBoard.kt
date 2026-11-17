package com.seedspacing.gauge.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seedspacing.gauge.core.theme.LocalSeedColors
import com.seedspacing.gauge.domain.model.BedLayout

@Composable
fun BedBoard(
    layout: BedLayout,
    lengthCm: Int,
    widthCm: Int,
    cropAId: String,
    tintA: Color,
    tintB: Color,
    progress: Float,
    modifier: Modifier = Modifier,
    dimmed: Boolean = false,
    emptyMessage: String? = null
) {
    val colors = LocalSeedColors.current
    val boardPad = 12.dp
    val boardBorder = 2.dp
    val frame = boardPad + boardBorder

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val availableW = if (maxWidth.value.isFinite()) maxWidth else 360.dp
        val availableH = if (maxHeight.value.isFinite()) maxHeight else 320.dp
        val cappedW = (availableW - 32.dp).coerceAtMost(380.dp).coerceAtLeast(120.dp)

        var innerW = cappedW - frame * 2
        var scale = if (lengthCm > 0) innerW.value / lengthCm else 1f
        var innerH = (widthCm * scale).dp
        val maxInnerH = (availableH - frame * 2).coerceAtLeast(80.dp)
        if (innerH > maxInnerH) {
            innerH = maxInnerH
            scale = if (widthCm > 0) innerH.value / widthCm else 1f
            innerW = (lengthCm * scale).dp
        }

        val boardW = innerW + frame * 2
        val boardH = innerH + frame * 2
        val shape = RoundedCornerShape(16.dp)
        val alpha = if (dimmed) 0.55f else 1f

        Box(
            modifier = Modifier
                .size(width = boardW, height = boardH)
                .background(colors.kraft.copy(alpha = 0.9f * alpha), shape)
                .border(boardBorder, colors.primary.copy(alpha = 0.35f * alpha), shape)
                .padding(boardPad),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = innerW, height = innerH)
                    .background(
                        Brush.linearGradient(listOf(colors.soilTop, colors.soilBottom)),
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Canvas(modifier = Modifier.size(width = innerW, height = innerH)) {
                    val pxPerCm = if (lengthCm > 0) size.width / lengthCm else 1f
                    val gridStep = 10f * pxPerCm
                    if (gridStep > 6f) {
                        var gx = gridStep
                        while (gx < size.width) {
                            drawLine(
                                color = colors.secondary.copy(alpha = 0.45f * alpha),
                                start = Offset(gx, 0f),
                                end = Offset(gx, size.height),
                                strokeWidth = 1f
                            )
                            gx += gridStep
                        }
                        var gy = gridStep
                        while (gy < size.height) {
                            drawLine(
                                color = colors.secondary.copy(alpha = 0.45f * alpha),
                                start = Offset(0f, gy),
                                end = Offset(size.width, gy),
                                strokeWidth = 1f
                            )
                            gy += gridStep
                        }
                    }

                    val rowCount = layout.rows.size
                    layout.rows.forEachIndexed { index, row ->
                        val reveal = (progress * rowCount - index).coerceIn(0f, 1f)
                        if (reveal <= 0f) return@forEachIndexed
                        val tint = if (row.cropId == cropAId) tintA else tintB
                        val radius = row.spacingCm * pxPerCm / 2f * 0.86f * reveal
                        if (radius <= 0.4f) return@forEachIndexed
                        val cy = row.yCm * pxPerCm
                        row.xPositionsCm.forEach { xCm ->
                            val cx = xCm * pxPerCm
                            drawCircle(
                                color = tint.copy(alpha = 0.18f * reveal * alpha),
                                radius = radius * 1.18f,
                                center = Offset(cx, cy)
                            )
                            drawCircle(
                                color = tint.copy(alpha = 0.85f * reveal * alpha),
                                radius = radius,
                                center = Offset(cx, cy)
                            )
                            if (radius > 3f) {
                                drawCircle(
                                    color = tint.copy(alpha = 0.9f * reveal * alpha),
                                    radius = radius,
                                    center = Offset(cx, cy),
                                    style = Stroke(width = 1.5f)
                                )
                            }
                        }
                    }
                }

                if (emptyMessage != null) {
                    Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 18.dp)
                    )
                }
            }
        }
    }
}
