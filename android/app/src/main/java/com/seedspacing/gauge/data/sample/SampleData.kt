package com.seedspacing.gauge.data.sample

import com.seedspacing.gauge.R
import com.seedspacing.gauge.data.local.SeedPreferences
import com.seedspacing.gauge.domain.model.Bed
import com.seedspacing.gauge.domain.model.Crop
import com.seedspacing.gauge.domain.model.CropFamily

object SampleData {

    val crops: List<Crop> = listOf(
        Crop("radish", R.string.crop_radish, CropFamily.ROOT, 5, 10, 28, 0xFFD45A7E),
        Crop("carrot", R.string.crop_carrot, CropFamily.ROOT, 7, 15, 70, 0xFFE07A3C),
        Crop("beetroot", R.string.crop_beetroot, CropFamily.ROOT, 10, 20, 56, 0xFFAB2A9F),
        Crop("onion", R.string.crop_onion, CropFamily.ROOT, 10, 20, 100, 0xFFC457BA),
        Crop("spinach", R.string.crop_spinach, CropFamily.LEAF, 12, 20, 40, 0xFF2E9E6B),
        Crop("lettuce", R.string.crop_lettuce, CropFamily.LEAF, 20, 5, 55, 0xFF28E091),
        Crop("kale", R.string.crop_kale, CropFamily.LEAF, 40, 15, 60, 0xFF12B873),
        Crop("pea", R.string.crop_pea, CropFamily.LEGUME, 8, 30, 65, 0xFF6FBF4A),
        Crop("bush_bean", R.string.crop_bush_bean, CropFamily.LEGUME, 15, 30, 55, 0xFF4F9E57),
        Crop("tomato", R.string.crop_tomato, CropFamily.FRUIT, 50, 10, 85, 0xFFD23B5E),
        Crop("cucumber", R.string.crop_cucumber, CropFamily.VINE, 35, 25, 60, 0xFF3FA7A0),
        Crop("zucchini", R.string.crop_zucchini, CropFamily.VINE, 60, 30, 50, 0xFF7E1C76)
    )

    fun starterBeds(prefs: SeedPreferences, now: Long): List<Bed> = listOf(
        Bed(
            id = "bed_kitchen_door",
            name = prefs.text(R.string.sample_bed_kitchen),
            lengthCm = 240,
            widthCm = 120,
            primaryCropId = "carrot",
            secondaryCropId = "lettuce",
            ratioA = 2,
            ratioB = 1,
            offsetRows = false,
            onHold = false,
            createdAt = now - 259200000L,
            notes = emptyList()
        ),
        Bed(
            id = "bed_kraft_shed",
            name = prefs.text(R.string.sample_bed_shed),
            lengthCm = 180,
            widthCm = 90,
            primaryCropId = "radish",
            secondaryCropId = null,
            ratioA = 1,
            ratioB = 1,
            offsetRows = true,
            onHold = false,
            createdAt = now - 86400000L,
            notes = emptyList()
        ),
        Bed(
            id = "bed_balcony_strip",
            name = prefs.text(R.string.sample_bed_balcony),
            lengthCm = 90,
            widthCm = 40,
            primaryCropId = "spinach",
            secondaryCropId = null,
            ratioA = 1,
            ratioB = 1,
            offsetRows = false,
            onHold = true,
            createdAt = now - 3600000L,
            notes = emptyList()
        )
    )
}
