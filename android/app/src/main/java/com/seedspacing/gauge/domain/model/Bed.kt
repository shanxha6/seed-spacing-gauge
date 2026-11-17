package com.seedspacing.gauge.domain.model

data class Note(
    val id: String,
    val text: String,
    val createdAt: Long
)

data class Bed(
    val id: String,
    val name: String,
    val lengthCm: Int,
    val widthCm: Int,
    val primaryCropId: String,
    val secondaryCropId: String?,
    val ratioA: Int,
    val ratioB: Int,
    val offsetRows: Boolean,
    val onHold: Boolean,
    val createdAt: Long,
    val notes: List<Note>
)
