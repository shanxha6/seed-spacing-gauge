package com.seedspacing.gauge.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.seedspacing.gauge.R

@Composable
fun AppBackground(
    overlay: List<Color>,
    blobColor: Color,
    modifier: Modifier = Modifier,
    blobAlpha: Float = 0.18f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = blobColor.copy(alpha = blobAlpha),
                radius = size.minDimension * 0.52f,
                center = Offset(size.width * 0.82f, size.height * 0.16f)
            )
            drawCircle(
                color = blobColor.copy(alpha = blobAlpha * 0.6f),
                radius = size.minDimension * 0.38f,
                center = Offset(size.width * 0.1f, size.height * 0.78f)
            )
        }
        content()
    }
}
