package com.seedspacing.gauge.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedspacing.gauge.R
import com.seedspacing.gauge.core.di.ServiceLocator
import com.seedspacing.gauge.core.theme.LocalSeedColors
import com.seedspacing.gauge.core.theme.SplashOverlay
import com.seedspacing.gauge.core.ui.AppBackground
import kotlin.math.abs

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val colors = LocalSeedColors.current
    val transition = rememberInfiniteTransition(label = "splash")

    val discScale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "disc"
    )
    val drift by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "drift"
    )
    val halo by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "halo"
    )
    val seedWave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart),
        label = "seeds"
    )

    AppBackground(
        overlay = SplashOverlay,
        blobColor = colors.accent,
        blobAlpha = 0.1f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(188.dp)
                    .graphicsLayer {
                        scaleX = discScale
                        scaleY = discScale
                    }
                    .background(SplashPaper.copy(alpha = 0.14f), CircleShape)
                    .border(1.dp, colors.accent.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(168.dp)
                        .background(SplashGreen.copy(alpha = halo * 0.25f), CircleShape)
                )
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(148.dp)
                        .graphicsLayer { translationY = drift }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            repeat(3) { rowIndex ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    repeat(7) { dotIndex ->
                        val phase = (seedWave + rowIndex * 0.3f + dotIndex * 0.12f) % 3f
                        val dotAlpha = 0.2f + 0.7f * (1f - abs(phase - 1.5f) / 1.5f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (dotIndex % 2 == 0) {
                                        SplashGreen.copy(alpha = dotAlpha)
                                    } else {
                                        SplashBlush.copy(alpha = dotAlpha)
                                    },
                                    CircleShape
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = stringResource(R.string.brand_line_one),
                style = MaterialTheme.typography.displaySmall,
                color = SplashPaper,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.brand_line_two),
                style = MaterialTheme.typography.displaySmall,
                color = SplashGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelSmall,
                color = SplashBlush,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(34.dp))

            LinearProgressIndicator(
                color = SplashGreen,
                trackColor = SplashBlush.copy(alpha = 0.3f),
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.splash_status),
                style = MaterialTheme.typography.labelSmall,
                color = SplashBlush,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private val SplashPaper = Color(0xFFF6EDF5)
private val SplashGreen = Color(0xFF28E091)
private val SplashBlush = Color(0xFFDDB0C3)
