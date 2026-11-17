package com.seedspacing.gauge.domain.usecase

import com.seedspacing.gauge.core.config.GameConfig
import kotlin.math.ceil

class SeedsNeededUseCase {
    operator fun invoke(plantCount: Int): Int {
        if (plantCount <= 0) return 0
        return ceil(plantCount * GameConfig.SEED_BUFFER).toInt()
    }
}
