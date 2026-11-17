package com.seedspacing.gauge.domain.repository

import com.seedspacing.gauge.domain.model.Crop
import com.seedspacing.gauge.domain.model.CropFamily

interface CropRepository {
    fun all(): List<Crop>
    fun byId(id: String): Crop?
    fun byFamily(family: CropFamily?): List<Crop>
}
