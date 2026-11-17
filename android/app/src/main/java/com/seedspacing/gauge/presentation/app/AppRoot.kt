package com.seedspacing.gauge.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedspacing.gauge.core.di.ServiceLocator
import com.seedspacing.gauge.core.navigation.Screen
import com.seedspacing.gauge.core.theme.AppTheme
import com.seedspacing.gauge.domain.model.ThemeMode
import com.seedspacing.gauge.presentation.bedsize.BedSizeScreen
import com.seedspacing.gauge.presentation.croppicker.CropPickerScreen
import com.seedspacing.gauge.presentation.game.BedLayoutScreen
import com.seedspacing.gauge.presentation.guide.CropGuideScreen
import com.seedspacing.gauge.presentation.menu.MenuScreen
import com.seedspacing.gauge.presentation.notes.SowingNotesScreen
import com.seedspacing.gauge.presentation.result.PlantingResultScreen
import com.seedspacing.gauge.presentation.settings.SettingsScreen
import com.seedspacing.gauge.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canPop by appViewModel.canPop.collectAsStateWithLifecycle()
    val themeMode by appViewModel.themeMode.collectAsStateWithLifecycle()

    val dark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    AppTheme(darkTheme = dark) {
        BackHandler(enabled = canPop) { appViewModel.back() }

        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                (fadeIn(tween(220)) + slideInVertically { it / 12 })
                    .togetherWith(fadeOut(tween(160)))
            },
            label = "screen"
        ) { current ->
            when (current) {
                Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.replaceWith(Screen.Menu) }
                )

                Screen.Menu -> MenuScreen(
                    onStartPlanning = { appViewModel.navigate(Screen.BedSize) },
                    onOpenGuide = { appViewModel.navigate(Screen.Guide) },
                    onOpenSettings = { appViewModel.navigate(Screen.Settings) },
                    onOpenBed = { appViewModel.navigate(Screen.Game) },
                    onOpenNotes = { bedId -> appViewModel.navigate(Screen.Notes(bedId)) }
                )

                Screen.BedSize -> BedSizeScreen(
                    onBack = { appViewModel.back() },
                    onContinue = { appViewModel.navigate(Screen.CropPicker) }
                )

                Screen.CropPicker -> CropPickerScreen(
                    onBack = { appViewModel.back() },
                    onContinue = { appViewModel.navigate(Screen.Game) }
                )

                Screen.Game -> BedLayoutScreen(
                    onHome = { appViewModel.resetToMenu() },
                    onEditSize = { appViewModel.navigate(Screen.BedSize) },
                    onPlanned = { bedId -> appViewModel.navigate(Screen.GameOver(bedId)) }
                )

                is Screen.GameOver -> PlantingResultScreen(
                    bedId = current.bedId,
                    onPlanAgain = { appViewModel.navigate(Screen.BedSize) },
                    onOpenNotes = { bedId -> appViewModel.navigate(Screen.Notes(bedId)) },
                    onMenu = { appViewModel.resetToMenu() }
                )

                is Screen.Notes -> SowingNotesScreen(
                    bedId = current.bedId,
                    onBack = { appViewModel.back() }
                )

                Screen.Guide -> CropGuideScreen(
                    onBack = { appViewModel.back() }
                )

                Screen.Settings -> SettingsScreen(
                    onBack = { appViewModel.back() }
                )
            }
        }
    }
}
