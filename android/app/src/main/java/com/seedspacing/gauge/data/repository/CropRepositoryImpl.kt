package com.seedspacing.gauge.data.repository

import com.seedspacing.gauge.data.sample.SampleData
import com.seedspacing.gauge.domain.model.Crop
import com.seedspacing.gauge.domain.model.CropFamily
import com.seedspacing.gauge.domain.repository.CropRepository

class CropRepositoryImpl : CropRepository {

    private val catalogue: List<Crop> = SampleData.crops

    override fun all(): List<Crop> = catalogue

    override fun byId(id: String): Crop? = catalogue.firstOrNull { it.id == id }

    override fun byFamily(family: CropFamily?): List<Crop> =
        if (family == null) catalogue else catalogue.filter { it.family == family }
}
