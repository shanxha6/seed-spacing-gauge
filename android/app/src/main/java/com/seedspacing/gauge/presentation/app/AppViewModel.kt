package com.seedspacing.gauge.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedspacing.gauge.core.navigation.Screen
import com.seedspacing.gauge.domain.model.ThemeMode
import com.seedspacing.gauge.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppViewModel(settings: SettingsRepository) : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _backStack = MutableStateFlow<List<Screen>>(emptyList())

    val canPop: StateFlow<Boolean> = _backStack
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val themeMode: StateFlow<ThemeMode> = settings.settings
        .map { it.theme }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash && current != target) {
            _backStack.value = _backStack.value + current
        }
        _screen.value = target
    }

    fun replaceWith(target: Screen) {
        _screen.value = target
    }

    fun resetToMenu() {
        _backStack.value = emptyList()
        _screen.value = Screen.Menu
    }

    fun back() {
        val stack = _backStack.value
        if (stack.isEmpty()) return
        _screen.value = stack[stack.size - 1]
        _backStack.value = stack.subList(0, stack.size - 1).toList()
    }
}
