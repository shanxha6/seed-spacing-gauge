package com.seedspacing.gauge.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val BED_MIN_CM = 30
    const val BED_MAX_CM = 600
    const val BED_STEP_CM = 10
    const val DEFAULT_LENGTH_CM = 240
    const val DEFAULT_WIDTH_CM = 120
    const val MAX_BEDS = 24
    const val SEED_BUFFER = 1.15f
    const val OFFSET_PITCH = 0.866f
    const val MAX_NOTE_CHARS = 240
    const val MAX_NAME_CHARS = 24
    const val MAX_ANIMATED_PLANTS = 1400
    const val REVEAL_STEPS = 30
    const val REVEAL_STEP_MS = 20L
    const val SAVE_BLOOM_MS = 360L
    const val CM_PER_INCH = 2.54f
    const val IMPERIAL_STEP_IN = 6

    val RATIOS: List<Pair<Int, Int>> = listOf(2 to 1, 1 to 1, 1 to 2, 3 to 1)

    val SIZE_PRESETS: List<Pair<Int, Int>> = listOf(120 to 60, 240 to 120, 300 to 150, 90 to 40)
}
