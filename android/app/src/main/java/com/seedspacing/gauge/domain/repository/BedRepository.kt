package com.seedspacing.gauge.domain.repository

import com.seedspacing.gauge.domain.model.Bed
import kotlinx.coroutines.flow.StateFlow

interface BedRepository {
    val beds: StateFlow<List<Bed>>
    fun find(id: String): Bed?
    fun store(bed: Bed): String
    fun addNote(bedId: String, text: String)
    fun removeNote(bedId: String, noteId: String)
    fun setOnHold(bedId: String, onHold: Boolean)
    fun clearAll()
}
