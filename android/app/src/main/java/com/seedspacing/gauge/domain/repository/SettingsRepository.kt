package com.seedspacing.gauge.domain.repository

import com.seedspacing.gauge.domain.model.AppSettings
import com.seedspacing.gauge.domain.model.ThemeMode
import com.seedspacing.gauge.domain.model.UnitSystem
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>
    fun setUnits(units: UnitSystem)
    fun setTheme(mode: ThemeMode)
    fun setHaptics(enabled: Boolean)
    fun setOffsetByDefault(enabled: Boolean)
}
