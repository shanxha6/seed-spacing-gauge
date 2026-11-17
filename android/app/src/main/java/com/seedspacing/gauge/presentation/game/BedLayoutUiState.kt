package com.seedspacing.gauge.presentation.game

import com.seedspacing.gauge.domain.model.BedLayout

data class CropBadge(
    val id: String,
    val nameRes: Int,
    val spriteRes: Int,
    val tint: Long,
    val count: Int
)

data class BedLayoutUiState(
    val ready: Boolean = false,
    val bedName: String = "",
    val lengthCm: Int = 0,
    val widthCm: Int = 0,
    val lengthText: String = "",
    val widthText: String = "",
    val imperial: Boolean = false,
    val layout: BedLayout = BedLayout(),
    val badges: List<CropBadge> = emptyList(),
    val primaryId: String = "",
    val primaryTint: Long = 0xFFAB2A9F,
    val secondaryTint: Long = 0xFF28E091,
    val mix: Boolean = false,
    val ratioIndex: Int = 0,
    val ratioText: String = "",
    val offsetRows: Boolean = false,
    val onHold: Boolean = false,
    val animated: Boolean = true,
    val reveal: Float = 0f,
    val bloom: Boolean = false,
    val haptics: Boolean = true,
    val tooSmallCropNameRes: Int? = null,
    val tooSmallSpacing: Int = 0
)

sealed interface BedLayoutEvent {
    data class Planned(val bedId: String) : BedLayoutEvent
}
