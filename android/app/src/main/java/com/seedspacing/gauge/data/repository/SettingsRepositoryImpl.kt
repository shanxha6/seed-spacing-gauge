package com.seedspacing.gauge.data.repository

import com.seedspacing.gauge.data.local.SeedPreferences
import com.seedspacing.gauge.domain.model.AppSettings
import com.seedspacing.gauge.domain.model.ThemeMode
import com.seedspacing.gauge.domain.model.UnitSystem
import com.seedspacing.gauge.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsRepositoryImpl(private val prefs: SeedPreferences) : SettingsRepository {

    private val state = MutableStateFlow(read())

    override val settings: StateFlow<AppSettings> = state.asStateFlow()

    override fun setUnits(units: UnitSystem) {
        prefs.writeString(SeedPreferences.KEY_UNITS, units.name)
        state.update { it.copy(units = units) }
    }

    override fun setTheme(mode: ThemeMode) {
        prefs.writeString(SeedPreferences.KEY_THEME, mode.name)
        state.update { it.copy(theme = mode) }
    }

    override fun setHaptics(enabled: Boolean) {
        prefs.writeBoolean(SeedPreferences.KEY_HAPTICS, enabled)
        state.update { it.copy(haptics = enabled) }
    }

    override fun setOffsetByDefault(enabled: Boolean) {
        prefs.writeBoolean(SeedPreferences.KEY_OFFSET, enabled)
        state.update { it.copy(offsetByDefault = enabled) }
    }

    private fun read(): AppSettings = AppSettings(
        units = readUnits(),
        theme = readTheme(),
        haptics = prefs.readBoolean(SeedPreferences.KEY_HAPTICS, true),
        offsetByDefault = prefs.readBoolean(SeedPreferences.KEY_OFFSET, false)
    )

    private fun readUnits(): UnitSystem {
        val raw = prefs.readString(SeedPreferences.KEY_UNITS, UnitSystem.METRIC.name)
        return UnitSystem.entries.firstOrNull { it.name == raw } ?: UnitSystem.METRIC
    }

    private fun readTheme(): ThemeMode {
        val raw = prefs.readString(SeedPreferences.KEY_THEME, ThemeMode.SYSTEM.name)
        return ThemeMode.entries.firstOrNull { it.name == raw } ?: ThemeMode.SYSTEM
    }
}
