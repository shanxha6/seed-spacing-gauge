package com.seedspacing.gauge.presentation.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedspacing.gauge.core.ui.familySprite
import com.seedspacing.gauge.domain.model.AppSettings
import com.seedspacing.gauge.domain.model.CropFamily
import com.seedspacing.gauge.domain.model.UnitSystem
import com.seedspacing.gauge.domain.repository.CropRepository
import com.seedspacing.gauge.domain.repository.SettingsRepository
import com.seedspacing.gauge.domain.usecase.FormatLengthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CropGuideViewModel(
    private val crops: CropRepository,
    settings: SettingsRepository,
    private val formatLength: FormatLengthUseCase
) : ViewModel() {

    private val familyFilter = MutableStateFlow(-1)

    val state: StateFlow<CropGuideUiState> =
        combine(settings.settings, familyFilter) { config, family ->
            build(config, family)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CropGuideUiState())

    fun selectFamily(index: Int) {
        familyFilter.value = if (familyFilter.value == index) -1 else index
    }

    private fun build(config: AppSettings, family: Int): CropGuideUiState {
        val selected = CropFamily.entries.getOrNull(family)
        return CropGuideUiState(
            rows = crops.byFamily(selected).map { crop ->
                GuideRowItem(
                    id = crop.id,
                    nameRes = crop.nameRes,
                    spriteRes = familySprite(crop.family),
                    tint = crop.tint,
                    spacingText = formatLength(crop.spacingCm, config.units),
                    depthText = formatLength.depth(crop.depthMm, config.units),
                    days = crop.daysToHarvest
                )
            },
            familyIndex = family,
            imperial = config.units == UnitSystem.IMPERIAL
        )
    }
}
