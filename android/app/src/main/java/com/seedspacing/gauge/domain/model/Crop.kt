package com.seedspacing.gauge.domain.model

enum class CropFamily { ROOT, LEAF, LEGUME, FRUIT, VINE }

data class Crop(
    val id: String,
    val nameRes: Int,
    val family: CropFamily,
    val spacingCm: Int,
    val depthMm: Int,
    val daysToHarvest: Int,
    val tint: Long
)
