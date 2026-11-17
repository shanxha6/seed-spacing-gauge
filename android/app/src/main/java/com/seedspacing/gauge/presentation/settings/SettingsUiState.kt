package com.seedspacing.gauge.presentation.settings

data class SettingsUiState(
    val unitIndex: Int = 0,
    val themeIndex: Int = 2,
    val haptics: Boolean = true,
    val offsetByDefault: Boolean = false,
    val bedCount: Int = 0,
    val confirmVisible: Boolean = false
)
