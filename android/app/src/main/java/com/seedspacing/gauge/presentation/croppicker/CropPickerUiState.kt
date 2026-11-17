package com.seedspacing.gauge.presentation.croppicker

data class CropItem(
    val id: String,
    val nameRes: Int,
    val spriteRes: Int,
    val tint: Long,
    val spacingText: String,
    val spacingCm: Int,
    val depthText: String,
    val days: Int
)

data class CropPickerUiState(
    val items: List<CropItem> = emptyList(),
    val secondaryItems: List<CropItem> = emptyList(),
    val familyIndex: Int = -1,
    val selectedId: String? = null,
    val secondaryId: String? = null,
    val mixEnabled: Boolean = false,
    val ratioIndex: Int = 0,
    val imperial: Boolean = false,
    val canContinue: Boolean = false
)
