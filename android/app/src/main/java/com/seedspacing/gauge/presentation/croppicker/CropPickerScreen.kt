package com.seedspacing.gauge.presentation.croppicker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedspacing.gauge.R
import com.seedspacing.gauge.core.config.GameConfig
import com.seedspacing.gauge.core.di.ServiceLocator
import com.seedspacing.gauge.core.theme.LocalSeedColors
import com.seedspacing.gauge.core.ui.AppBackground
import com.seedspacing.gauge.core.ui.ChipItem
import com.seedspacing.gauge.core.ui.ChipRow
import com.seedspacing.gauge.core.ui.CropRow
import com.seedspacing.gauge.core.ui.PrimaryButton
import com.seedspacing.gauge.core.ui.ScreenHeader
import com.seedspacing.gauge.core.ui.familyLabel
import com.seedspacing.gauge.core.ui.familySprite
import com.seedspacing.gauge.domain.model.CropFamily

@Composable
fun CropPickerScreen(onBack: () -> Unit, onContinue: () -> Unit) {
    val cropPickerViewModel: CropPickerViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by cropPickerViewModel.state.collectAsStateWithLifecycle()

    CropPickerContent(
        state = state,
        onBack = onBack,
        onContinue = onContinue,
        onFamily = { cropPickerViewModel.selectFamily(it) },
        onSelectCrop = { cropPickerViewModel.selectPrimary(it) },
        onSelectSecondary = { cropPickerViewModel.selectSecondary(it) },
        onToggleMix = { cropPickerViewModel.toggleMix(it) },
        onRatio = { cropPickerViewModel.selectRatio(it) }
    )
}

@Composable
private fun CropPickerContent(
    state: CropPickerUiState,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onFamily: (Int) -> Unit,
    onSelectCrop: (String) -> Unit,
    onSelectSecondary: (String) -> Unit,
    onToggleMix: (Boolean) -> Unit,
    onRatio: (Int) -> Unit
) {
    val colors = LocalSeedColors.current
    val selectedLabel = stringResource(R.string.state_selected)
    val unselectedLabel = stringResource(R.string.state_not_selected)
    val unitLabel = stringResource(if (state.imperial) R.string.unit_inch_short else R.string.unit_cm_short)

    val familyChips = CropFamily.entries.map {
        ChipItem(stringResource(familyLabel(it)), familySprite(it))
    }
    val ratioChips = GameConfig.RATIOS.map { ChipItem(it.first.toString() + RATIO_SEPARATOR + it.second.toString()) }

    AppBackground(
        overlay = listOf(
            colors.paperTop.copy(alpha = 0.96f),
            colors.paperMid.copy(alpha = 0.92f),
            colors.paperDeep.copy(alpha = 0.95f)
        ),
        blobColor = colors.accent,
        blobAlpha = 0.1f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.crop_picker_title),
                onBack = onBack,
                trailing = {
                    Box(
                        modifier = Modifier
                            .background(colors.primary.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.step_two),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.primary
                        )
                    }
                }
            )

            ChipRow(
                items = familyChips,
                selectedIndex = state.familyIndex,
                onSelect = onFamily,
                selectedLabel = selectedLabel,
                unselectedLabel = unselectedLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 10.dp)
            ) {
                items(state.items, key = { it.id }) { item ->
                    CropRow(
                        name = stringResource(item.nameRes),
                        meta = stringResource(
                            R.string.crop_meta,
                            item.spacingText,
                            unitLabel,
                            item.depthText,
                            item.days
                        ),
                        spacingText = item.spacingText,
                        spriteRes = item.spriteRes,
                        tint = Color(item.tint),
                        selected = item.id == state.selectedId,
                        selectedLabel = selectedLabel,
                        unselectedLabel = unselectedLabel,
                        onClick = { onSelectCrop(item.id) }
                    )
                }
            }

            MixSection(
                state = state,
                ratioChips = ratioChips,
                selectedLabel = selectedLabel,
                unselectedLabel = unselectedLabel,
                onToggleMix = onToggleMix,
                onSelectSecondary = onSelectSecondary,
                onRatio = onRatio
            )

            Text(
                text = stringResource(R.string.hint_tap_crop),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.draw_the_bed),
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
private fun MixSection(
    state: CropPickerUiState,
    ratioChips: List<ChipItem>,
    selectedLabel: String,
    unselectedLabel: String,
    onToggleMix: (Boolean) -> Unit,
    onSelectSecondary: (String) -> Unit,
    onRatio: (Int) -> Unit
) {
    val colors = LocalSeedColors.current
    val switchState = stringResource(if (state.mixEnabled) R.string.state_on else R.string.state_off)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card.copy(alpha = 0.94f), RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.mix_second_crop),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary
            )
            Switch(
                checked = state.mixEnabled,
                onCheckedChange = onToggleMix,
                modifier = Modifier.semantics { stateDescription = switchState }
            )
        }

        AnimatedVisibility(visible = state.mixEnabled) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ChipRow(
                    items = state.secondaryItems.map {
                        ChipItem(stringResource(it.nameRes), it.spriteRes)
                    },
                    selectedIndex = state.secondaryItems.indexOfFirst { it.id == state.secondaryId },
                    onSelect = { index ->
                        state.secondaryItems.getOrNull(index)?.let { onSelectSecondary(it.id) }
                    },
                    selectedLabel = selectedLabel,
                    unselectedLabel = unselectedLabel,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.ratio_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    ChipRow(
                        items = ratioChips,
                        selectedIndex = state.ratioIndex,
                        onSelect = onRatio,
                        selectedLabel = selectedLabel,
                        unselectedLabel = unselectedLabel,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private const val RATIO_SEPARATOR = " : "
