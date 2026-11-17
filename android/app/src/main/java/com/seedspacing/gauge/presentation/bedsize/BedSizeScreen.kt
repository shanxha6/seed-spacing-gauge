package com.seedspacing.gauge.presentation.bedsize

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedspacing.gauge.R
import com.seedspacing.gauge.core.di.ServiceLocator
import com.seedspacing.gauge.core.theme.LocalSeedColors
import com.seedspacing.gauge.core.ui.AppBackground
import com.seedspacing.gauge.core.ui.ChipItem
import com.seedspacing.gauge.core.ui.ChipRow
import com.seedspacing.gauge.core.ui.PrimaryButton
import com.seedspacing.gauge.core.ui.ScreenHeader
import com.seedspacing.gauge.core.ui.SeedPacketCard
import com.seedspacing.gauge.core.ui.StepperRow

@Composable
fun BedSizeScreen(onBack: () -> Unit, onContinue: () -> Unit) {
    val bedSizeViewModel: BedSizeViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by bedSizeViewModel.state.collectAsStateWithLifecycle()

    BedSizeContent(
        state = state,
        onBack = onBack,
        onContinue = onContinue,
        onDecreaseLength = { bedSizeViewModel.changeLength(false) },
        onIncreaseLength = { bedSizeViewModel.changeLength(true) },
        onDecreaseWidth = { bedSizeViewModel.changeWidth(false) },
        onIncreaseWidth = { bedSizeViewModel.changeWidth(true) },
        onPreset = { bedSizeViewModel.applyPreset(it) },
        onRename = { bedSizeViewModel.rename(it) }
    )
}

@Composable
private fun BedSizeContent(
    state: BedSizeUiState,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onDecreaseLength: () -> Unit,
    onIncreaseLength: () -> Unit,
    onDecreaseWidth: () -> Unit,
    onIncreaseWidth: () -> Unit,
    onPreset: (Int) -> Unit,
    onRename: (String) -> Unit
) {
    val colors = LocalSeedColors.current
    val unitLabel = stringResource(if (state.imperial) R.string.unit_inch_short else R.string.unit_cm_short)
    val aspect by animateFloatAsState(
        targetValue = state.aspect,
        animationSpec = tween(180),
        label = "bedAspect"
    )
    val presets = listOf(
        ChipItem(stringResource(R.string.preset_small)),
        ChipItem(stringResource(R.string.preset_classic)),
        ChipItem(stringResource(R.string.preset_wide)),
        ChipItem(stringResource(R.string.preset_planter))
    )

    AppBackground(
        overlay = listOf(
            colors.paperTop.copy(alpha = 0.95f),
            colors.paperMid.copy(alpha = 0.93f),
            colors.paperDeep.copy(alpha = 0.95f)
        ),
        blobColor = colors.secondary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.bed_size_title),
                onBack = onBack,
                trailing = {
                    StepPill(text = stringResource(R.string.step_one))
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SeedPacketCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .height(140.dp)
                                .aspectRatio(aspect.coerceAtLeast(0.3f))
                                .background(colors.cardAlt, RoundedCornerShape(10.dp))
                                .border(1.dp, colors.primary.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
                        )
                        Text(
                            text = stringResource(
                                if (state.imperial) R.string.bed_dims_imperial else R.string.bed_dims_metric,
                                state.lengthText,
                                state.widthText
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    StepperRow(
                        label = stringResource(R.string.label_length),
                        value = state.lengthText,
                        unitLabel = unitLabel,
                        onDecrease = onDecreaseLength,
                        onIncrease = onIncreaseLength,
                        decreaseEnabled = state.canDecreaseLength,
                        increaseEnabled = state.canIncreaseLength
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    StepperRow(
                        label = stringResource(R.string.label_width),
                        value = state.widthText,
                        unitLabel = unitLabel,
                        onDecrease = onDecreaseWidth,
                        onIncrease = onIncreaseWidth,
                        decreaseEnabled = state.canDecreaseWidth,
                        increaseEnabled = state.canIncreaseWidth
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ChipRow(
                        items = presets,
                        selectedIndex = state.presetIndex,
                        onSelect = onPreset,
                        selectedLabel = stringResource(R.string.state_selected),
                        unselectedLabel = stringResource(R.string.state_not_selected),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onRename,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        label = { Text(text = stringResource(R.string.bed_name_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Text(
                text = stringResource(R.string.hint_centre_to_centre),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.pick_crop),
                onClick = onContinue,
                enabled = state.canContinue,
                leadingIcon = R.drawable.ic_forward,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            )
        }
    }
}

@Composable
private fun StepPill(text: String) {
    val colors = LocalSeedColors.current
    Box(
        modifier = Modifier
            .background(colors.primary.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = colors.primary
        )
    }
}
