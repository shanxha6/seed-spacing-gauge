package com.seedspacing.gauge.presentation.result

import com.seedspacing.gauge.domain.model.BedLayout

data class ResultBadge(
    val nameRes: Int,
    val spriteRes: Int,
    val tint: Long,
    val count: Int
)

data class PlantingResultUiState(
    val ready: Boolean = false,
    val bedId: String = "",
    val bedName: String = "",
    val total: Int = 0,
    val seedsNeeded: Int = 0,
    val layout: BedLayout = BedLayout(),
    val lengthCm: Int = 0,
    val widthCm: Int = 0,
    val lengthText: String = "",
    val widthText: String = "",
    val imperial: Boolean = false,
    val depthText: String = "",
    val days: Int = 0,
    val badges: List<ResultBadge> = emptyList(),
    val primaryId: String = "",
    val primaryTint: Long = 0xFFAB2A9F,
    val secondaryTint: Long = 0xFF28E091,
    val nextIndex: Int = 1
)
