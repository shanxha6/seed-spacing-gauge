package com.seedspacing.gauge.presentation.croppicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedspacing.gauge.core.ui.familySprite
import com.seedspacing.gauge.domain.model.AppSettings
import com.seedspacing.gauge.domain.model.BedDraft
import com.seedspacing.gauge.domain.model.Crop
import com.seedspacing.gauge.domain.model.CropFamily
import com.seedspacing.gauge.domain.model.UnitSystem
import com.seedspacing.gauge.domain.repository.BedDraftStore
import com.seedspacing.gauge.domain.repository.CropRepository
import com.seedspacing.gauge.domain.repository.SettingsRepository
import com.seedspacing.gauge.domain.usecase.FormatLengthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CropPickerViewModel(
    private val crops: CropRepository,
    settings: SettingsRepository,
    private val drafts: BedDraftStore,
    private val formatLength: FormatLengthUseCase
) : ViewModel() {

    private val familyFilter = MutableStateFlow(-1)

    val state: StateFlow<CropPickerUiState> =
        combine(drafts.draft, settings.settings, familyFilter) { draft, config, family ->
            build(draft, config, family)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CropPickerUiState())

    fun selectFamily(index: Int) {
        familyFilter.value = if (familyFilter.value == index) -1 else index
    }

    fun selectPrimary(id: String) {
        viewModelScope.launch {
            drafts.edit {
                if (it.secondaryCropId == id) {
                    it.copy(primaryCropId = id, secondaryCropId = null)
                } else {
                    it.copy(primaryCropId = id)
                }
            }
        }
    }

    fun selectSecondary(id: String) {
        viewModelScope.launch {
            drafts.edit {
                when {
                    it.primaryCropId == id -> it
                    it.secondaryCropId == id -> it.copy(secondaryCropId = null)
                    else -> it.copy(secondaryCropId = id)
                }
            }
        }
    }

    fun toggleMix(enabled: Boolean) {
        viewModelScope.launch {
            drafts.edit {
                if (enabled) {
                    val fallback = crops.all().firstOrNull { crop -> crop.id != it.primaryCropId }
                    it.copy(secondaryCropId = it.secondaryCropId ?: fallback?.id)
                } else {
                    it.copy(secondaryCropId = null)
                }
            }
        }
    }

    fun selectRatio(index: Int) {
        viewModelScope.launch {
            drafts.edit { it.copy(ratioIndex = index) }
        }
    }

    private fun build(draft: BedDraft, config: AppSettings, family: Int): CropPickerUiState {
        val families = CropFamily.entries
        val selectedFamily = families.getOrNull(family)
        val visible = crops.byFamily(selectedFamily).map { toItem(it, config) }
        val secondary = crops.all()
            .filter { it.id != draft.primaryCropId }
            .map { toItem(it, config) }
        val mixOn = draft.secondaryCropId != null
        return CropPickerUiState(
            items = visible,
            secondaryItems = secondary,
            familyIndex = family,
            selectedId = draft.primaryCropId,
            secondaryId = draft.secondaryCropId,
            mixEnabled = mixOn,
            ratioIndex = draft.ratioIndex,
            imperial = config.units == UnitSystem.IMPERIAL,
            canContinue = crops.byId(draft.primaryCropId) != null &&
                (!mixOn || crops.byId(draft.secondaryCropId.orEmpty()) != null)
        )
    }

    private fun toItem(crop: Crop, config: AppSettings): CropItem = CropItem(
        id = crop.id,
        nameRes = crop.nameRes,
        spriteRes = familySprite(crop.family),
        tint = crop.tint,
        spacingText = formatLength(crop.spacingCm, config.units),
        spacingCm = crop.spacingCm,
        depthText = formatLength.depth(crop.depthMm, config.units),
        days = crop.daysToHarvest
    )
}
