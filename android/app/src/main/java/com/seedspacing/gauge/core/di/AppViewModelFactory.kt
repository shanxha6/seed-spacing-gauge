package com.seedspacing.gauge.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.seedspacing.gauge.domain.repository.BedDraftStore
import com.seedspacing.gauge.domain.repository.BedRepository
import com.seedspacing.gauge.domain.repository.CropRepository
import com.seedspacing.gauge.domain.repository.SettingsRepository
import com.seedspacing.gauge.domain.usecase.CalculateBedLayoutUseCase
import com.seedspacing.gauge.domain.usecase.FormatLengthUseCase
import com.seedspacing.gauge.domain.usecase.SeedsNeededUseCase
import com.seedspacing.gauge.presentation.app.AppViewModel
import com.seedspacing.gauge.presentation.bedsize.BedSizeViewModel
import com.seedspacing.gauge.presentation.croppicker.CropPickerViewModel
import com.seedspacing.gauge.presentation.game.BedLayoutViewModel
import com.seedspacing.gauge.presentation.guide.CropGuideViewModel
import com.seedspacing.gauge.presentation.menu.MenuViewModel
import com.seedspacing.gauge.presentation.notes.SowingNotesViewModel
import com.seedspacing.gauge.presentation.result.PlantingResultViewModel
import com.seedspacing.gauge.presentation.settings.SettingsViewModel
import com.seedspacing.gauge.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val beds: BedRepository,
    private val crops: CropRepository,
    private val settings: SettingsRepository,
    private val drafts: BedDraftStore,
    private val calculateLayout: CalculateBedLayoutUseCase,
    private val seedsNeeded: SeedsNeededUseCase,
    private val formatLength: FormatLengthUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel(settings)

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(beds, crops, settings, drafts, calculateLayout, formatLength)

            modelClass.isAssignableFrom(BedSizeViewModel::class.java) ->
                BedSizeViewModel(settings, drafts, formatLength)

            modelClass.isAssignableFrom(CropPickerViewModel::class.java) ->
                CropPickerViewModel(crops, settings, drafts, formatLength)

            modelClass.isAssignableFrom(BedLayoutViewModel::class.java) ->
                BedLayoutViewModel(beds, crops, settings, drafts, calculateLayout, formatLength)

            modelClass.isAssignableFrom(PlantingResultViewModel::class.java) ->
                PlantingResultViewModel(beds, crops, settings, drafts, calculateLayout, seedsNeeded, formatLength)

            modelClass.isAssignableFrom(SowingNotesViewModel::class.java) ->
                SowingNotesViewModel(beds, crops, calculateLayout)

            modelClass.isAssignableFrom(CropGuideViewModel::class.java) ->
                CropGuideViewModel(crops, settings, formatLength)

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(beds, settings)

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
