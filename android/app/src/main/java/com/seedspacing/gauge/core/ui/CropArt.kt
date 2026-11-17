package com.seedspacing.gauge.core.ui

import com.seedspacing.gauge.R
import com.seedspacing.gauge.domain.model.CropFamily
import com.seedspacing.gauge.domain.model.FillVerdict

fun familySprite(family: CropFamily): Int = when (family) {
    CropFamily.ROOT -> R.drawable.sprite_1
    CropFamily.LEAF -> R.drawable.sprite_2
    CropFamily.LEGUME -> R.drawable.sprite_3
    CropFamily.FRUIT -> R.drawable.sprite_4
    CropFamily.VINE -> R.drawable.sprite_5
}

fun familyLabel(family: CropFamily): Int = when (family) {
    CropFamily.ROOT -> R.string.family_root
    CropFamily.LEAF -> R.string.family_leaf
    CropFamily.LEGUME -> R.string.family_legume
    CropFamily.FRUIT -> R.string.family_fruit
    CropFamily.VINE -> R.string.family_vine
}

fun verdictLabel(verdict: FillVerdict): Int = when (verdict) {
    FillVerdict.ROOM_TO_SPARE -> R.string.verdict_room
    FillVerdict.BALANCED -> R.string.verdict_balanced
    FillVerdict.PACKED_TIGHT -> R.string.verdict_packed
}

fun verdictTip(verdict: FillVerdict): Int = when (verdict) {
    FillVerdict.ROOM_TO_SPARE -> R.string.tip_room
    FillVerdict.BALANCED -> R.string.tip_balanced
    FillVerdict.PACKED_TIGHT -> R.string.tip_packed
}
