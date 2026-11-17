package com.seedspacing.gauge.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedspacing.gauge.domain.model.ThemeMode
import com.seedspacing.gauge.domain.model.UnitSystem
import com.seedspacing.gauge.domain.repository.BedRepository
import com.seedspacing.gauge.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val beds: BedRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val confirmVisible = MutableStateFlow(false)

    val state: StateFlow<SettingsUiState> =
        combine(settings.settings, beds.beds, confirmVisible) { config, list, confirm ->
            SettingsUiState(
                unitIndex = UnitSystem.entries.indexOf(config.units),
                themeIndex = ThemeMode.entries.indexOf(config.theme),
                haptics = config.haptics,
                offsetByDefault = config.offsetByDefault,
                bedCount = list.size,
                confirmVisible = confirm
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun selectUnits(index: Int) {
        val value = UnitSystem.entries.getOrNull(index) ?: return
        viewModelScope.launch { settings.setUnits(value) }
    }

    fun selectTheme(index: Int) {
        val value = ThemeMode.entries.getOrNull(index) ?: return
        viewModelScope.launch { settings.setTheme(value) }
    }

    fun setHaptics(enabled: Boolean) {
        viewModelScope.launch { settings.setHaptics(enabled) }
    }

    fun setOffsetByDefault(enabled: Boolean) {
        viewModelScope.launch { settings.setOffsetByDefault(enabled) }
    }

    fun askReset() {
        confirmVisible.value = true
    }

    fun dismissReset() {
        confirmVisible.value = false
    }

    fun confirmReset() {
        viewModelScope.launch {
            beds.clearAll()
            confirmVisible.value = false
        }
    }
}
