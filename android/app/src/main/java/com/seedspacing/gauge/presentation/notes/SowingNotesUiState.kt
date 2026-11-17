package com.seedspacing.gauge.presentation.notes

data class NoteItem(
    val id: String,
    val text: String,
    val dateText: String
)

data class SowingNotesUiState(
    val bedId: String = "",
    val bedName: String = "",
    val cropNameRes: Int? = null,
    val spriteRes: Int? = null,
    val plantCount: Int = 0,
    val notes: List<NoteItem> = emptyList(),
    val draftText: String = "",
    val canSave: Boolean = false
)
