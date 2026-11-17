package com.seedspacing.gauge.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedspacing.gauge.R
import com.seedspacing.gauge.core.di.ServiceLocator
import com.seedspacing.gauge.core.theme.LocalSeedColors
import com.seedspacing.gauge.core.ui.AppBackground
import com.seedspacing.gauge.core.ui.BedBoard
import com.seedspacing.gauge.core.ui.FillGauge
import com.seedspacing.gauge.core.ui.PrimaryButton
import com.seedspacing.gauge.core.ui.SecondaryButton
import com.seedspacing.gauge.core.ui.StatCard
import com.seedspacing.gauge.core.ui.verdictLabel

@Composable
fun BedLayoutScreen(
    onHome: () -> Unit,
    onEditSize: () -> Unit,
    onPlanned: (String) -> Unit
) {
    val bedLayoutViewModel: BedLayoutViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by bedLayoutViewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    DisposableEffect(Unit) {
        bedLayoutViewModel.resume()
        onDispose { bedLayoutViewModel.pause() }
    }

    LaunchedEffect(Unit) {
        bedLayoutViewModel.events.collect { event ->
            when (event) {
                is BedLayoutEvent.Planned -> {
                    if (state.haptics) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onPlanned(event.bedId)
                }
            }
        }
    }

    BedLayoutContent(
        state = state,
        onHome = onHome,
        onEditSize = onEditSize,
        onToggleHold = { bedLayoutViewModel.toggleHold() },
        onToggleOffset = { bedLayoutViewModel.toggleOffsetRows() },
        onRatioBack = { bedLayoutViewModel.shiftRatio(false) },
        onRatioForward = { bedLayoutViewModel.shiftRatio(true) },
        onSave = { bedLayoutViewModel.saveBed() }
    )
}

@Composable
private fun BedLayoutContent(
    state: BedLayoutUiState,
    onHome: () -> Unit,
    onEditSize: () -> Unit,
    onToggleHold: () -> Unit,
    onToggleOffset: () -> Unit,
    onRatioBack: () -> Unit,
    onRatioForward: () -> Unit,
    onSave: () -> Unit
) {
    val colors = LocalSeedColors.current
    val dims = stringResource(
        if (state.imperial) R.string.bed_dims_imperial else R.string.bed_dims_metric,
        state.lengthText,
        state.widthText
    )
    val holdState = stringResource(if (state.onHold) R.string.on_hold else R.string.planned)

    AppBackground(
        overlay = listOf(
            colors.paperMid.copy(alpha = 0.94f),
            colors.paperDeep.copy(alpha = 0.9f),
            colors.paperDeep.copy(alpha = 0.96f)
        ),
        blobColor = colors.primary,
        blobAlpha = 0.09f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onHome, modifier = Modifier.size(48.dp)) {
                    Image(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = stringResource(R.string.cd_home),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.bedName,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dims,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
                IconButton(
                    onClick = onToggleHold,
                    modifier = Modifier
                        .size(48.dp)
                        .semantics { stateDescription = holdState }
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_hold),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            ReadoutStrip(state = state)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 12.dp, bottom = 152.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BedBoard(
                    layout = state.layout,
                    lengthCm = state.lengthCm.coerceAtLeast(1),
                    widthCm = state.widthCm.coerceAtLeast(1),
                    cropAId = state.primaryId,
                    tintA = Color(state.primaryTint),
                    tintB = Color(state.secondaryTint),
                    progress = state.reveal,
                    dimmed = state.onHold,
                    emptyMessage = state.tooSmallCropNameRes?.let {
                        stringResource(R.string.too_small, stringResource(it), state.tooSmallSpacing)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    state.badges.forEach { badge ->
                        LegendChip(
                            spriteRes = badge.spriteRes,
                            label = stringResource(
                                R.string.legend_entry,
                                stringResource(badge.nameRes),
                                badge.count
                            ),
                            tint = Color(badge.tint)
                        )
                    }
                }

                FillGauge(
                    percent = state.layout.fillPercent,
                    verdict = state.layout.verdict,
                    verdictText = stringResource(verdictLabel(state.layout.verdict)),
                    percentText = stringResource(R.string.percent_value, state.layout.fillPercent),
                    dimmed = state.onHold,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        FloatingControls(
            state = state,
            onToggleOffset = onToggleOffset,
            onRatioBack = onRatioBack,
            onRatioForward = onRatioForward,
            onSave = onSave,
            onEditSize = onEditSize,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .safeDrawingPadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
        )

        SaveBloom(visible = state.bloom, modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun ReadoutStrip(state: BedLayoutUiState) {
    val colors = LocalSeedColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card.copy(alpha = 0.92f), RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.mix && state.badges.size > 1) {
            StatCard(
                value = state.badges[0].count.toString(),
                label = stringResource(R.string.stat_crop_a),
                accent = Color(state.badges[0].tint),
                compact = true,
                live = true,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = state.badges[1].count.toString(),
                label = stringResource(R.string.stat_crop_b),
                accent = Color(state.badges[1].tint),
                compact = true,
                live = true,
                modifier = Modifier.weight(1f)
            )
        } else {
            StatCard(
                value = state.layout.total.toString(),
                label = stringResource(R.string.stat_plants),
                accent = colors.primary,
                compact = true,
                live = true,
                modifier = Modifier.weight(1f)
            )
        }
        StatCard(
            value = stringResource(
                R.string.rows_by_cols,
                state.layout.rowCount,
                state.layout.colCount
            ),
            label = stringResource(R.string.stat_grid),
            accent = colors.accentDark,
            compact = true,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = stringResource(R.string.percent_value, state.layout.fillPercent),
            label = stringResource(R.string.stat_fill),
            accent = colors.secondary,
            compact = true,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LegendChip(spriteRes: Int, label: String, tint: Color) {
    val colors = LocalSeedColors.current
    Row(
        modifier = Modifier
            .background(colors.card.copy(alpha = 0.9f), RoundedCornerShape(14.dp))
            .border(1.dp, tint.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(spriteRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FloatingControls(
    state: BedLayoutUiState,
    onToggleOffset: () -> Unit,
    onRatioBack: () -> Unit,
    onRatioForward: () -> Unit,
    onSave: () -> Unit,
    onEditSize: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalSeedColors.current
    val shape = RoundedCornerShape(20.dp)
    val offsetState = stringResource(if (state.offsetRows) R.string.state_on else R.string.state_off)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, shape, ambientColor = colors.primary.copy(alpha = 0.2f))
            .background(colors.card.copy(alpha = 0.97f), shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .background(
                        if (state.offsetRows) colors.accent.copy(alpha = 0.16f) else colors.cardAlt,
                        RoundedCornerShape(14.dp)
                    )
                    .border(
                        1.dp,
                        if (state.offsetRows) colors.accent else colors.secondary.copy(alpha = 0.7f),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onToggleOffset() }
                    .padding(horizontal = 12.dp)
                    .semantics { stateDescription = offsetState },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.offset_rows),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.offsetRows) colors.accentDark else colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            if (state.mix) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(onClick = onRatioBack, modifier = Modifier.size(48.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_minus),
                            contentDescription = stringResource(R.string.cd_ratio_down),
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = state.ratioText,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.primary
                    )
                    IconButton(onClick = onRatioForward, modifier = Modifier.size(48.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_plus),
                            contentDescription = stringResource(R.string.cd_ratio_up),
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrimaryButton(
                text = stringResource(R.string.save_bed),
                onClick = onSave,
                enabled = state.ready,
                height = 52.dp,
                modifier = Modifier.weight(1.6f)
            )
            SecondaryButton(
                text = stringResource(R.string.edit),
                onClick = onEditSize,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SaveBloom(visible: Boolean, modifier: Modifier = Modifier) {
    val colors = LocalSeedColors.current
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.4f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "bloom"
    )
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .background(colors.accent.copy(alpha = 0.26f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check_circle),
                contentDescription = null,
                tint = colors.accentDark,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}
