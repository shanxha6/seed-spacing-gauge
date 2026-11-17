package com.seedspacing.gauge.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedspacing.gauge.core.config.GameConfig
import com.seedspacing.gauge.core.ui.familySprite
import com.seedspacing.gauge.domain.model.toDraft
import com.seedspacing.gauge.domain.repository.BedRepository
import com.seedspacing.gauge.domain.repository.CropRepository
import com.seedspacing.gauge.domain.usecase.CalculateBedLayoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SowingNotesViewModel(
    private val beds: BedRepository,
    private val crops: CropRepository,
    private val calculateLayout: CalculateBedLayoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SowingNotesUiState())
    val state: StateFlow<SowingNotesUiState> = _state.asStateFlow()

    private val stamp = SimpleDateFormat(DATE_PATTERN, Locale.US)

    fun bind(bedId: String) {
        viewModelScope.launch {
            refresh(bedId)
        }
    }

    fun updateDraft(value: String) {
        val trimmed = value.take(GameConfig.MAX_NOTE_CHARS)
        _state.update { it.copy(draftText = trimmed, canSave = trimmed.isNotBlank()) }
    }

    fun commitNote() {
        val current = _state.value
        if (current.draftText.isBlank()) return
        viewModelScope.launch {
            beds.addNote(current.bedId, current.draftText)
            _state.update { it.copy(draftText = "", canSave = false) }
            refresh(current.bedId)
        }
    }

    fun deleteNote(noteId: String) {
        val bedId = _state.value.bedId
        viewModelScope.launch {
            beds.removeNote(bedId, noteId)
            refresh(bedId)
        }
    }

    private fun refresh(bedId: String) {
        val bed = beds.find(bedId) ?: return
        val cropA = crops.byId(bed.primaryCropId)
        val cropB = bed.secondaryCropId?.let { crops.byId(it) }
        val count = if (cropA == null) 0 else calculateLayout(bed.toDraft(), cropA, cropB).total
        _state.update {
            it.copy(
                bedId = bed.id,
                bedName = bed.name,
                cropNameRes = cropA?.nameRes,
                spriteRes = cropA?.let { crop -> familySprite(crop.family) },
                plantCount = count,
                notes = bed.notes.map { note ->
                    NoteItem(
                        id = note.id,
                        text = note.text,
                        dateText = stamp.format(Date(note.createdAt))
                    )
                }
            )
        }
    }

    private companion object {
        const val DATE_PATTERN = "dd MMM yyyy"
    }
}
