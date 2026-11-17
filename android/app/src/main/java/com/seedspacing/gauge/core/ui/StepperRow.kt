package com.seedspacing.gauge.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.seedspacing.gauge.R
import com.seedspacing.gauge.core.theme.LocalSeedColors
import kotlinx.coroutines.delay

@Composable
fun StepperRow(
    label: String,
    value: String,
    unitLabel: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    decreaseEnabled: Boolean = true,
    increaseEnabled: Boolean = true
) {
    val colors = LocalSeedColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StepperKey(
                iconRes = R.drawable.ic_minus,
                enabled = decreaseEnabled,
                onAction = onDecrease,
                description = stringResource(R.string.cd_decrease, label)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                AnimatedContent(
                    targetState = value,
                    transitionSpec = {
                        (fadeIn(tween(140)) + slideInVertically { it / 3 })
                            .togetherWith(fadeOut(tween(100)) + slideOutVertically { -it / 3 })
                    },
                    label = "stepperValue"
                ) { shown ->
                    Text(
                        text = shown,
                        style = MaterialTheme.typography.displaySmall,
                        color = colors.primary
                    )
                }
                Box(modifier = Modifier.width(6.dp))
                Text(
                    text = unitLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            StepperKey(
                iconRes = R.drawable.ic_plus,
                enabled = increaseEnabled,
                onAction = onIncrease,
                description = stringResource(R.string.cd_increase, label)
            )
        }
    }
}

@Composable
private fun StepperKey(
    iconRes: Int,
    enabled: Boolean,
    onAction: () -> Unit,
    description: String
) {
    val colors = LocalSeedColors.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = RoundedCornerShape(14.dp)
    val state = stringResource(if (enabled) R.string.state_enabled else R.string.state_disabled)

    LaunchedEffect(pressed, enabled) {
        if (pressed && enabled) {
            delay(420)
            while (true) {
                onAction()
                delay(90)
            }
        }
    }

    IconButton(
        onClick = onAction,
        enabled = enabled,
        interactionSource = interaction,
        modifier = Modifier
            .size(width = 64.dp, height = 48.dp)
            .background(
                if (enabled) colors.accent.copy(alpha = 0.16f) else colors.secondary.copy(alpha = 0.22f),
                shape
            )
            .border(
                1.dp,
                if (enabled) colors.accent.copy(alpha = 0.9f) else colors.secondary.copy(alpha = 0.4f),
                shape
            )
            .semantics { stateDescription = state }
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = description,
            tint = if (enabled) colors.accentDark else colors.textMuted,
            modifier = Modifier.size(24.dp)
        )
    }
}
