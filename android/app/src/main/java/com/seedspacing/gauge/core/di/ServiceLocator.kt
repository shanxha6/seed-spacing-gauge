package com.seedspacing.gauge.core.di

import android.content.Context
import com.seedspacing.gauge.data.local.SeedPreferences
import com.seedspacing.gauge.data.repository.BedDraftStoreImpl
import com.seedspacing.gauge.data.repository.BedRepositoryImpl
import com.seedspacing.gauge.data.repository.CropRepositoryImpl
import com.seedspacing.gauge.data.repository.SettingsRepositoryImpl
import com.seedspacing.gauge.domain.usecase.CalculateBedLayoutUseCase
import com.seedspacing.gauge.domain.usecase.FormatLengthUseCase
import com.seedspacing.gauge.domain.usecase.SeedsNeededUseCase

object ServiceLocator {

    private var factory: AppViewModelFactory? = null

    fun init(context: Context) {
        if (factory != null) return
        val preferences = SeedPreferences(context)
        factory = AppViewModelFactory(
            beds = BedRepositoryImpl(preferences),
            crops = CropRepositoryImpl(),
            settings = SettingsRepositoryImpl(preferences),
            drafts = BedDraftStoreImpl(),
            calculateLayout = CalculateBedLayoutUseCase(),
            seedsNeeded = SeedsNeededUseCase(),
            formatLength = FormatLengthUseCase()
        )
    }

    val viewModelFactory: AppViewModelFactory
        get() = checkNotNull(factory)
}
