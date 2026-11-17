package com.seedspacing.gauge.presentation.settings

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
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
import com.seedspacing.gauge.core.ui.ScreenHeader
import com.seedspacing.gauge.core.ui.SecondaryButton
import com.seedspacing.gauge.core.ui.SeedPacketCard
import com.seedspacing.gauge.presentation.dialog.ConfirmResetDialog

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        onBack = onBack,
        onUnits = { settingsViewModel.selectUnits(it) },
        onTheme = { settingsViewModel.selectTheme(it) },
        onHaptics = { settingsViewModel.setHaptics(it) },
        onOffset = { settingsViewModel.setOffsetByDefault(it) },
        onAskReset = { settingsViewModel.askReset() },
        onDismissReset = { settingsViewModel.dismissReset() },
        onConfirmReset = { settingsViewModel.confirmReset() }
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onUnits: (Int) -> Unit,
    onTheme: (Int) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onOffset: (Boolean) -> Unit,
    onAskReset: () -> Unit,
    onDismissReset: () -> Unit,
    onConfirmReset: () -> Unit
) {
    val colors = LocalSeedColors.current
    val selectedLabel = stringResource(R.string.state_selected)
    val unselectedLabel = stringResource(R.string.state_not_selected)
    val unitChips = listOf(
        ChipItem(stringResource(R.string.unit_cm)),
        ChipItem(stringResource(R.string.unit_inch))
    )
    val themeChips = listOf(
        ChipItem(stringResource(R.string.theme_light)),
        ChipItem(stringResource(R.string.theme_dark)),
        ChipItem(stringResource(R.string.theme_system))
    )

    if (state.confirmVisible) {
        ConfirmResetDialog(onConfirm = onConfirmReset, onDismiss = onDismissReset)
    }

    AppBackground(
        overlay = listOf(
            colors.paperTop.copy(alpha = 0.96f),
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
                title = stringResource(R.string.settings),
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SeedPacketCard(modifier = Modifier.fillMaxWidth(), padding = 16.dp) {
                    SettingLabel(text = stringResource(R.string.units))
                    ChipRow(
                        items = unitChips,
                        selectedIndex = state.unitIndex,
                        onSelect = onUnits,
                        selectedLabel = selectedLabel,
                        unselectedLabel = unselectedLabel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = colors.secondary.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingLabel(text = stringResource(R.string.theme))
                    ChipRow(
                        items = themeChips,
                        selectedIndex = state.themeIndex,
                        onSelect = onTheme,
                        selectedLabel = selectedLabel,
                        unselectedLabel = unselectedLabel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = colors.secondary.copy(alpha = 0.4f))

                    SwitchRow(
                        label = stringResource(R.string.haptics),
                        checked = state.haptics,
                        onChange = onHaptics
                    )

                    HorizontalDivider(color = colors.secondary.copy(alpha = 0.4f))

                    SwitchRow(
                        label = stringResource(R.string.offset_default),
                        checked = state.offsetByDefault,
                        onChange = onOffset
                    )
                }

                SeedPacketCard(modifier = Modifier.fillMaxWidth(), padding = 16.dp) {
                    SettingLabel(text = stringResource(R.string.danger_zone))
                    Text(
                        text = stringResource(R.string.danger_hint, state.bedCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    SecondaryButton(
                        text = stringResource(R.string.reset_all_beds),
                        onClick = onAskReset,
                        enabled = state.bedCount > 0,
                        tint = colors.danger,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(36.dp)
                            .alpha(0.5f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.settings_footer),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Box(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun SettingLabel(text: String) {
    val colors = LocalSeedColors.current
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = colors.textSecondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val colors = LocalSeedColors.current
    val switchState = stringResource(if (checked) R.string.state_on else R.string.state_off)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            modifier = Modifier.semantics { stateDescription = switchState }
        )
    }
}
