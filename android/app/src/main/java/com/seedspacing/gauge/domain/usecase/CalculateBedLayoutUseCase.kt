package com.seedspacing.gauge.domain.usecase

import com.seedspacing.gauge.core.config.GameConfig
import com.seedspacing.gauge.domain.model.BedDraft
import com.seedspacing.gauge.domain.model.BedLayout
import com.seedspacing.gauge.domain.model.Crop
import com.seedspacing.gauge.domain.model.FillVerdict
import com.seedspacing.gauge.domain.model.PlantRow
import kotlin.math.floor
import kotlin.math.roundToInt

class CalculateBedLayoutUseCase {

    operator fun invoke(draft: BedDraft, cropA: Crop, cropB: Crop?): BedLayout {
        val lengthCm = draft.lengthCm.toFloat()
        val widthCm = draft.widthCm.toFloat()
        if (lengthCm <= 0f || widthCm <= 0f) return BedLayout()

        val rows = ArrayList<PlantRow>()
        var totalA = 0
        var totalB = 0
        var widest = 0
        var cursor = 0f
        var index = 0
        var occupied = 0f

        while (index < MAX_ROWS) {
            val useSecondary = cropB != null && isSecondaryRow(index, draft.ratioA, draft.ratioB)
            val crop = if (useSecondary && cropB != null) cropB else cropA
            val spacing = crop.spacingCm.toFloat()
            if (spacing <= 0f) break

            val pitch = if (draft.offsetRows) spacing * GameConfig.OFFSET_PITCH else spacing
            if (cursor + pitch > widthCm + TOLERANCE) break

            val baseCols = floor(lengthCm / spacing).toInt()
            if (baseCols <= 0) break

            val shifted = draft.offsetRows && index % 2 == 1
            val cols = if (shifted) baseCols - 1 else baseCols
            if (cols <= 0) {
                cursor += pitch
                index++
                continue
            }

            val positions = ArrayList<Float>(cols)
            val shift = if (shifted) spacing / 2f else 0f
            for (i in 0 until cols) {
                positions.add(spacing * (i + 0.5f) + shift)
            }

            rows.add(
                PlantRow(
                    cropId = crop.id,
                    yCm = cursor + pitch / 2f,
                    spacingCm = crop.spacingCm,
                    xPositionsCm = positions
                )
            )
            if (useSecondary) totalB += cols else totalA += cols
            occupied += cols * spacing * spacing
            if (cols > widest) widest = cols

            cursor += pitch
            index++
        }

        val area = lengthCm * widthCm
        val fill = if (area <= 0f) 0 else ((occupied * 100f) / area).roundToInt().coerceIn(0, 120)
        val verdict = when {
            fill < 60 -> FillVerdict.ROOM_TO_SPARE
            fill <= 85 -> FillVerdict.BALANCED
            else -> FillVerdict.PACKED_TIGHT
        }

        return BedLayout(
            rows = rows,
            totalA = totalA,
            totalB = totalB,
            rowCount = rows.size,
            colCount = widest,
            fillPercent = fill,
            verdict = verdict
        )
    }

    private fun isSecondaryRow(index: Int, ratioA: Int, ratioB: Int): Boolean {
        val a = ratioA.coerceAtLeast(1)
        val b = ratioB.coerceAtLeast(1)
        return index % (a + b) >= a
    }

    private companion object {
        const val MAX_ROWS = 400
        const val TOLERANCE = 0.001f
    }
}
