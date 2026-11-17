package com.seedspacing.gauge.domain.model

enum class FillVerdict { ROOM_TO_SPARE, BALANCED, PACKED_TIGHT }

data class PlantRow(
    val cropId: String,
    val yCm: Float,
    val spacingCm: Int,
    val xPositionsCm: List<Float>
)

data class BedLayout(
    val rows: List<PlantRow> = emptyList(),
    val totalA: Int = 0,
    val totalB: Int = 0,
    val rowCount: Int = 0,
    val colCount: Int = 0,
    val fillPercent: Int = 0,
    val verdict: FillVerdict = FillVerdict.ROOM_TO_SPARE
) {
    val total: Int get() = totalA + totalB
}
