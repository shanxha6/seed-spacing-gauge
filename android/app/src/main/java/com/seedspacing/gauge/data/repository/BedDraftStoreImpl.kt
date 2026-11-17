package com.seedspacing.gauge.data.repository

import com.seedspacing.gauge.core.config.GameConfig
import com.seedspacing.gauge.domain.model.Bed
import com.seedspacing.gauge.domain.model.BedDraft
import com.seedspacing.gauge.domain.repository.BedDraftStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BedDraftStoreImpl : BedDraftStore {

    private val state = MutableStateFlow(BedDraft())

    override val draft: StateFlow<BedDraft> = state.asStateFlow()

    override fun edit(transform: (BedDraft) -> BedDraft) {
        state.update(transform)
    }

    override fun startFresh(name: String, offsetRows: Boolean) {
        state.value = BedDraft(name = name, offsetRows = offsetRows)
    }

    override fun loadFrom(bed: Bed) {
        val index = GameConfig.RATIOS.indexOfFirst { it.first == bed.ratioA && it.second == bed.ratioB }
        state.value = BedDraft(
            bedId = bed.id,
            name = bed.name,
            lengthCm = bed.lengthCm,
            widthCm = bed.widthCm,
            primaryCropId = bed.primaryCropId,
            secondaryCropId = bed.secondaryCropId,
            ratioIndex = if (index >= 0) index else 0,
            offsetRows = bed.offsetRows,
            onHold = bed.onHold
        )
    }
}
