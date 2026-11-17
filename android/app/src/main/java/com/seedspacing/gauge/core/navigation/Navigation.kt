package com.seedspacing.gauge.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object BedSize : Screen
    data object CropPicker : Screen
    data object Game : Screen
    data class GameOver(val bedId: String) : Screen
    data class Notes(val bedId: String) : Screen
    data object Guide : Screen
    data object Settings : Screen
}
