package com.seedspacing.gauge.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedspacing.gauge.core.ui.familySprite
import com.seedspacing.gauge.domain.model.AppSettings
import com.seedspacing.gauge.domain.model.Bed
import com.seedspacing.gauge.domain.model.UnitSystem
import com.seedspacing.gauge.domain.model.toDraft
import com.seedspacing.gauge.domain.repository.BedDraftStore
import com.seedspacing.gauge.domain.repository.BedRepository
import com.seedspacing.gauge.domain.repository.CropRepository
import com.seedspacing.gauge.domain.repository.SettingsRepository
import com.seedspacing.gauge.domain.usecase.CalculateBedLayoutUseCase
import com.seedspacing.gauge.domain.usecase.FormatLengthUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MenuViewModel(
    private val beds: BedRepository,
    private val crops: CropRepository,
    private val settings: SettingsRepository,
    private val drafts: BedDraftStore,
    private val calculateLayout: CalculateBedLayoutUseCase,
    private val formatLength: FormatLengthUseCase
) : ViewModel() {

    val state: StateFlow<MenuUiState> = combine(beds.beds, settings.settings) { list, config ->
        build(list, config)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MenuUiState())

    fun startNewBed(defaultName: String) {
        viewModelScope.launch {
            drafts.startFresh(defaultName, settings.settings.value.offsetByDefault)
        }
    }

    fun openBed(bedId: String) {
        viewModelScope.launch {
            beds.find(bedId)?.let { drafts.loadFrom(it) }
        }
    }

    private fun build(list: List<Bed>, config: AppSettings): MenuUiState {
        var plants = 0
        var squareCm = 0L
        val catalogue = crops.all()
        val summaries = list.map { bed ->
            val cropA = crops.byId(bed.primaryCropId) ?: catalogue.first()
            val cropB = bed.secondaryCropId?.let { crops.byId(it) }
            val layout = calculateLayout(bed.toDraft(), cropA, cropB)
            plants += layout.total
            squareCm += bed.lengthCm.toLong() * bed.widthCm.toLong()
            BedSummary(
                id = bed.id,
                name = bed.name,
                lengthText = formatLength(bed.lengthCm, config.units),
                widthText = formatLength(bed.widthCm, config.units),
                cropAName = cropA.nameRes,
                cropBName = cropB?.nameRes,
                spriteRes = familySprite(cropA.family),
                plantCount = layout.total,
                onHold = bed.onHold
            )
        }
        return MenuUiState(
            beds = summaries,
            totalPlants = plants,
            areaText = formatLength.area(squareCm, config.units),
            imperial = config.units == UnitSystem.IMPERIAL,
            nextIndex = list.size + 1
        )
    }
}
