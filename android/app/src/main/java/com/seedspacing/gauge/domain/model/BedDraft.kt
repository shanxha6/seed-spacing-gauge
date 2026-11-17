package com.seedspacing.gauge.domain.model

import com.seedspacing.gauge.core.config.GameConfig

data class BedDraft(
    val bedId: String? = null,
    val name: String = "",
    val lengthCm: Int = GameConfig.DEFAULT_LENGTH_CM,
    val widthCm: Int = GameConfig.DEFAULT_WIDTH_CM,
    val primaryCropId: String = "carrot",
    val secondaryCropId: String? = null,
    val ratioIndex: Int = 0,
    val offsetRows: Boolean = false,
    val onHold: Boolean = false
) {
    private val ratio: Pair<Int, Int>
        get() = GameConfig.RATIOS[ratioIndex.coerceIn(0, GameConfig.RATIOS.size - 1)]

    val ratioA: Int get() = ratio.first
    val ratioB: Int get() = ratio.second
}

fun ratioIndexOf(ratioA: Int, ratioB: Int): Int {
    val found = GameConfig.RATIOS.indexOfFirst { it.first == ratioA && it.second == ratioB }
    return if (found >= 0) found else 0
}

fun Bed.toDraft(): BedDraft = BedDraft(
    bedId = id,
    name = name,
    lengthCm = lengthCm,
    widthCm = widthCm,
    primaryCropId = primaryCropId,
    secondaryCropId = secondaryCropId,
    ratioIndex = ratioIndexOf(ratioA, ratioB),
    offsetRows = offsetRows,
    onHold = onHold
)

fun BedDraft.toBed(createdAt: Long, existing: Bed?): Bed = Bed(
    id = bedId ?: existing?.id ?: "",
    name = name,
    lengthCm = lengthCm,
    widthCm = widthCm,
    primaryCropId = primaryCropId,
    secondaryCropId = secondaryCropId,
    ratioA = ratioA,
    ratioB = ratioB,
    offsetRows = offsetRows,
    onHold = onHold,
    createdAt = existing?.createdAt ?: createdAt,
    notes = existing?.notes ?: emptyList()
)
