package com.seedspacing.gauge.presentation.menu

data class BedSummary(
    val id: String,
    val name: String,
    val lengthText: String,
    val widthText: String,
    val cropAName: Int,
    val cropBName: Int?,
    val spriteRes: Int,
    val plantCount: Int,
    val onHold: Boolean
)

data class MenuUiState(
    val beds: List<BedSummary> = emptyList(),
    val totalPlants: Int = 0,
    val areaText: String = "",
    val imperial: Boolean = false,
    val nextIndex: Int = 1
)
