package com.seedspacing.gauge.presentation.bedsize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedspacing.gauge.core.config.GameConfig
import com.seedspacing.gauge.domain.model.AppSettings
import com.seedspacing.gauge.domain.model.BedDraft
import com.seedspacing.gauge.domain.model.UnitSystem
import com.seedspacing.gauge.domain.repository.BedDraftStore
import com.seedspacing.gauge.domain.repository.SettingsRepository
import com.seedspacing.gauge.domain.usecase.FormatLengthUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class BedSizeViewModel(
    private val settings: SettingsRepository,
    private val drafts: BedDraftStore,
    private val formatLength: FormatLengthUseCase
) : ViewModel() {

    val state: StateFlow<BedSizeUiState> = combine(drafts.draft, settings.settings) { draft, config ->
        build(draft, config)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BedSizeUiState())

    fun changeLength(up: Boolean) {
        val step = stepCm()
        viewModelScope.launch {
            drafts.edit { it.copy(lengthCm = shift(it.lengthCm, if (up) step else -step)) }
        }
    }

    fun changeWidth(up: Boolean) {
        val step = stepCm()
        viewModelScope.launch {
            drafts.edit { it.copy(widthCm = shift(it.widthCm, if (up) step else -step)) }
        }
    }

    fun applyPreset(index: Int) {
        val preset = GameConfig.SIZE_PRESETS.getOrNull(index) ?: return
        viewModelScope.launch {
            drafts.edit { it.copy(lengthCm = preset.first, widthCm = preset.second) }
        }
    }

    fun rename(value: String) {
        val trimmed = value.take(GameConfig.MAX_NAME_CHARS)
        viewModelScope.launch {
            drafts.edit { it.copy(name = trimmed) }
        }
    }

    fun keepExistingName(fallback: String) {
        viewModelScope.launch {
            drafts.edit { if (it.name.isBlank()) it.copy(name = fallback) else it }
        }
    }

    private fun stepCm(): Int =
        if (settings.settings.value.units == UnitSystem.IMPERIAL) {
            (GameConfig.IMPERIAL_STEP_IN * GameConfig.CM_PER_INCH).roundToInt()
        } else {
            GameConfig.BED_STEP_CM
        }

    private fun shift(value: Int, delta: Int): Int =
        (value + delta).coerceIn(GameConfig.BED_MIN_CM, GameConfig.BED_MAX_CM)

    private fun build(draft: BedDraft, config: AppSettings): BedSizeUiState {
        val step = if (config.units == UnitSystem.IMPERIAL) {
            (GameConfig.IMPERIAL_STEP_IN * GameConfig.CM_PER_INCH).roundToInt()
        } else {
            GameConfig.BED_STEP_CM
        }
        val presetIndex = GameConfig.SIZE_PRESETS.indexOfFirst {
            it.first == draft.lengthCm && it.second == draft.widthCm
        }
        return BedSizeUiState(
            name = draft.name,
            lengthCm = draft.lengthCm,
            widthCm = draft.widthCm,
            lengthText = formatLength(draft.lengthCm, config.units),
            widthText = formatLength(draft.widthCm, config.units),
            imperial = config.units == UnitSystem.IMPERIAL,
            canDecreaseLength = draft.lengthCm - step >= GameConfig.BED_MIN_CM,
            canIncreaseLength = draft.lengthCm + step <= GameConfig.BED_MAX_CM,
            canDecreaseWidth = draft.widthCm - step >= GameConfig.BED_MIN_CM,
            canIncreaseWidth = draft.widthCm + step <= GameConfig.BED_MAX_CM,
            presetIndex = presetIndex,
            aspect = if (draft.widthCm > 0) {
                (draft.lengthCm.toFloat() / draft.widthCm.toFloat()).coerceIn(0.25f, 6f)
            } else {
                2f
            },
            canContinue = draft.lengthCm >= GameConfig.BED_MIN_CM && draft.widthCm >= GameConfig.BED_MIN_CM
        )
    }
}
