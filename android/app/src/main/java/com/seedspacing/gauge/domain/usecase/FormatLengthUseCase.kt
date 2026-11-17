package com.seedspacing.gauge.domain.usecase

import com.seedspacing.gauge.core.config.GameConfig
import com.seedspacing.gauge.domain.model.UnitSystem
import kotlin.math.roundToInt

class FormatLengthUseCase {

    operator fun invoke(centimetres: Int, units: UnitSystem): String {
        if (units == UnitSystem.METRIC) return centimetres.toString()
        val halves = (centimetres / GameConfig.CM_PER_INCH * 2f).roundToInt()
        return halfStep(halves)
    }

    fun depth(millimetres: Int, units: UnitSystem): String {
        if (units == UnitSystem.METRIC) return tenths(millimetres)
        val halves = (millimetres / 10f / GameConfig.CM_PER_INCH * 2f).roundToInt()
        return halfStep(halves)
    }

    fun area(squareCentimetres: Long, units: UnitSystem): String {
        if (squareCentimetres <= 0L) return tenths(0)
        return if (units == UnitSystem.METRIC) {
            tenths((squareCentimetres / 1000f).roundToInt())
        } else {
            val squareInches = squareCentimetres / (GameConfig.CM_PER_INCH * GameConfig.CM_PER_INCH)
            tenths((squareInches / 14.4f).roundToInt())
        }
    }

    private fun halfStep(halves: Int): String {
        val whole = halves / 2
        return if (halves % 2 == 0) whole.toString() else whole.toString() + FRACTION
    }

    private fun tenths(value: Int): String {
        val whole = value / 10
        val rest = value % 10
        return if (rest == 0) whole.toString() else whole.toString() + SEPARATOR + rest.toString()
    }

    private companion object {
        const val FRACTION = ".5"
        const val SEPARATOR = "."
    }
}
