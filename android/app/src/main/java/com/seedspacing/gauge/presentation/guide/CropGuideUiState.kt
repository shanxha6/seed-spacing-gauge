package com.seedspacing.gauge.presentation.guide

data class GuideRowItem(
    val id: String,
    val nameRes: Int,
    val spriteRes: Int,
    val tint: Long,
    val spacingText: String,
    val depthText: String,
    val days: Int
)

data class CropGuideUiState(
    val rows: List<GuideRowItem> = emptyList(),
    val familyIndex: Int = -1,
    val imperial: Boolean = false
)
