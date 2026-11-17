package com.seedspacing.gauge.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedspacing.gauge.core.ui.familySprite
import com.seedspacing.gauge.domain.model.Bed
import com.seedspacing.gauge.domain.model.UnitSystem
import com.seedspacing.gauge.domain.model.toDraft
import com.seedspacing.gauge.domain.repository.BedDraftStore
import com.seedspacing.gauge.domain.repository.BedRepository
import com.seedspacing.gauge.domain.repository.CropRepository
import com.seedspacing.gauge.domain.repository.SettingsRepository
import com.seedspacing.gauge.domain.usecase.CalculateBedLayoutUseCase
import com.seedspacing.gauge.domain.usecase.FormatLengthUseCase
import com.seedspacing.gauge.domain.usecase.SeedsNeededUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlantingResultViewModel(
    private val beds: BedRepository,
    private val crops: CropRepository,
    private val settings: SettingsRepository,
    private val drafts: BedDraftStore,
    private val calculateLayout: CalculateBedLayoutUseCase,
    private val seedsNeeded: SeedsNeededUseCase,
    private val formatLength: FormatLengthUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlantingResultUiState())
    val state: StateFlow<PlantingResultUiState> = _state.asStateFlow()

    fun bind(bedId: String) {
        viewModelScope.launch {
            val bed = beds.find(bedId) ?: return@launch
            _state.value = compose(bed)
        }
    }

    fun startNewBed(defaultName: String) {
        viewModelScope.launch {
            drafts.startFresh(defaultName, settings.settings.value.offsetByDefault)
        }
    }

    private fun compose(bed: Bed): PlantingResultUiState {
        val config = settings.settings.value
        val catalogue = crops.all()
        val cropA = crops.byId(bed.primaryCropId) ?: catalogue.first()
        val cropB = bed.secondaryCropId?.let { crops.byId(it) }
        val layout = calculateLayout(bed.toDraft(), cropA, cropB)

        val badges = buildList {
            add(
                ResultBadge(
                    nameRes = cropA.nameRes,
                    spriteRes = familySprite(cropA.family),
                    tint = cropA.tint,
                    count = layout.totalA
                )
            )
            if (cropB != null) {
                add(
                    ResultBadge(
                        nameRes = cropB.nameRes,
                        spriteRes = familySprite(cropB.family),
                        tint = cropB.tint,
                        count = layout.totalB
                    )
                )
            }
        }

        return PlantingResultUiState(
            ready = true,
            bedId = bed.id,
            bedName = bed.name,
            total = layout.total,
            seedsNeeded = seedsNeeded(layout.total),
            layout = layout,
            lengthCm = bed.lengthCm,
            widthCm = bed.widthCm,
            lengthText = formatLength(bed.lengthCm, config.units),
            widthText = formatLength(bed.widthCm, config.units),
            imperial = config.units == UnitSystem.IMPERIAL,
            depthText = formatLength.depth(cropA.depthMm, config.units),
            days = cropA.daysToHarvest,
            badges = badges,
            primaryId = cropA.id,
            primaryTint = cropA.tint,
            secondaryTint = cropB?.tint ?: cropA.tint,
            nextIndex = beds.beds.value.size + 1
        )
    }
}
