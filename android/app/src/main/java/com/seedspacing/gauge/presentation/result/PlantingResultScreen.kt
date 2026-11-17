package com.seedspacing.gauge.presentation.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
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
import com.seedspacing.gauge.core.ui.SeedPacketCard
import com.seedspacing.gauge.core.ui.verdictLabel
import com.seedspacing.gauge.core.ui.verdictTip

@Composable
fun PlantingResultScreen(
    bedId: String,
    onPlanAgain: () -> Unit,
    onOpenNotes: (String) -> Unit,
    onMenu: () -> Unit
) {
    val resultViewModel: PlantingResultViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by resultViewModel.state.collectAsStateWithLifecycle()
    val nextName = stringResource(R.string.default_bed_name, state.nextIndex)

    LaunchedEffect(bedId) {
        resultViewModel.bind(bedId)
    }

    PlantingResultContent(
        state = state,
        onPlanAgain = {
            resultViewModel.startNewBed(nextName)
            onPlanAgain()
        },
        onOpenNotes = { onOpenNotes(bedId) },
        onMenu = onMenu
    )
}

@Composable
private fun PlantingResultContent(
    state: PlantingResultUiState,
    onPlanAgain: () -> Unit,
    onOpenNotes: () -> Unit,
    onMenu: () -> Unit
) {
    val colors = LocalSeedColors.current
    val dims = stringResource(
        if (state.imperial) R.string.bed_dims_imperial else R.string.bed_dims_metric,
        state.lengthText,
        state.widthText
    )

    AppBackground(
        overlay = listOf(
            colors.paperTop.copy(alpha = 0.94f),
            colors.paperMid.copy(alpha = 0.92f),
            colors.paperDeep.copy(alpha = 0.95f)
        ),
        blobColor = colors.accent,
        blobAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp)
                )
                Text(
                    text = stringResource(R.string.bed_saved),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = state.total.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = colors.primary,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
                Text(
                    text = stringResource(R.string.plants_fit),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                SeedPacketCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BedBoard(
                            layout = state.layout,
                            lengthCm = state.lengthCm.coerceAtLeast(1),
                            widthCm = state.widthCm.coerceAtLeast(1),
                            cropAId = state.primaryId,
                            tintA = Color(state.primaryTint),
                            tintB = Color(state.secondaryTint),
                            progress = 1f,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ResultRow(label = stringResource(R.string.row_bed), value = dims)
                    ResultRow(
                        label = stringResource(R.string.row_layout),
                        value = stringResource(
                            R.string.layout_value,
                            state.layout.rowCount,
                            state.layout.colCount
                        )
                    )
                    ResultRow(
                        label = stringResource(R.string.row_fill),
                        value = stringResource(
                            R.string.fill_value,
                            state.layout.fillPercent,
                            stringResource(verdictLabel(state.layout.verdict))
                        )
                    )
                    ResultRow(
                        label = stringResource(R.string.row_depth),
                        value = stringResource(
                            if (state.imperial) R.string.depth_imperial else R.string.depth_metric,
                            state.depthText
                        )
                    )
                    ResultRow(
                        label = stringResource(R.string.row_days),
                        value = state.days.toString()
                    )
                    state.badges.forEach { badge ->
                        ResultRow(
                            label = stringResource(badge.nameRes),
                            value = badge.count.toString(),
                            accent = Color(badge.tint)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FillGauge(
                        percent = state.layout.fillPercent,
                        verdict = state.layout.verdict,
                        verdictText = stringResource(verdictLabel(state.layout.verdict)),
                        percentText = stringResource(R.string.percent_value, state.layout.fillPercent)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                colors.secondary.copy(alpha = 0.25f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.seeds_needed),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                        Text(
                            text = state.seedsNeeded.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(verdictTip(state.layout.verdict)),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
                text = stringResource(R.string.hint_saved_local),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.plan_again),
                onClick = onPlanAgain,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.add_notes),
                    onClick = onOpenNotes,
                    leadingImage = null,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.menu),
                    onClick = onMenu,
                    leadingImage = R.drawable.icon_home,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, accent: Color? = null) {
    val colors = LocalSeedColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = accent ?: colors.textPrimary
        )
    }
}
