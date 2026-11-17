package com.seedspacing.gauge.domain.repository

import com.seedspacing.gauge.domain.model.Bed
import com.seedspacing.gauge.domain.model.BedDraft
import kotlinx.coroutines.flow.StateFlow

interface BedDraftStore {
    val draft: StateFlow<BedDraft>
    fun edit(transform: (BedDraft) -> BedDraft)
    fun startFresh(name: String, offsetRows: Boolean)
    fun loadFrom(bed: Bed)
}
