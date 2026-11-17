package com.seedspacing.gauge.presentation.bedsize

data class BedSizeUiState(
    val name: String = "",
    val lengthCm: Int = 0,
    val widthCm: Int = 0,
    val lengthText: String = "",
    val widthText: String = "",
    val imperial: Boolean = false,
    val canDecreaseLength: Boolean = false,
    val canIncreaseLength: Boolean = false,
    val canDecreaseWidth: Boolean = false,
    val canIncreaseWidth: Boolean = false,
    val presetIndex: Int = -1,
    val aspect: Float = 2f,
    val canContinue: Boolean = false
)
