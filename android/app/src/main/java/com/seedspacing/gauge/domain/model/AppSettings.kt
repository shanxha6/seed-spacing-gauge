package com.seedspacing.gauge.domain.model

enum class UnitSystem { METRIC, IMPERIAL }

enum class ThemeMode { LIGHT, DARK, SYSTEM }

data class AppSettings(
    val units: UnitSystem = UnitSystem.METRIC,
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val haptics: Boolean = true,
    val offsetByDefault: Boolean = false
)
