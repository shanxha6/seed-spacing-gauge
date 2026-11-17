package com.seedspacing.gauge.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedspacing.gauge.core.config.GameConfig
import com.seedspacing.gauge.core.ui.familySprite
import com.seedspacing.gauge.domain.model.AppSettings
import com.seedspacing.gauge.domain.model.BedDraft
import com.seedspacing.gauge.domain.model.UnitSystem
import com.seedspacing.gauge.domain.model.toBed
import com.seedspacing.gauge.domain.repository.BedDraftStore
import com.seedspacing.gauge.domain.repository.BedRepository
import com.seedspacing.gauge.domain.repository.CropRepository
import com.seedspacing.gauge.domain.repository.SettingsRepository
import com.seedspacing.gauge.domain.usecase.CalculateBedLayoutUseCase
import com.seedspacing.gauge.domain.usecase.FormatLengthUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BedLayoutViewModel(
    private val beds: BedRepository,
    private val crops: CropRepository,
    private val settings: SettingsRepository,
    private val drafts: BedDraftStore,
    private val calculateLayout: CalculateBedLayoutUseCase,
    private val formatLength: FormatLengthUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BedLayoutUiState())
    val state: StateFlow<BedLayoutUiState> = _state.asStateFlow()

    private val _events = Channel<BedLayoutEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var revealJob: Job? = null

    init {
        viewModelScope.launch {
            combine(drafts.draft, settings.settings) { draft, config -> draft to config }
                .collect { pair -> recompute(pair.first, pair.second) }
        }
    }

    fun resume() {
        if (_state.value.reveal < 1f) startReveal(_state.value.animated)
    }

    fun pause() {
        revealJob?.cancel()
        revealJob = null
    }

    fun toggleOffsetRows() {
        viewModelScope.launch {
            drafts.edit { it.copy(offsetRows = !it.offsetRows) }
        }
    }

    fun shiftRatio(forward: Boolean) {
        viewModelScope.launch {
            drafts.edit {
                val size = GameConfig.RATIOS.size
                val next = (it.ratioIndex + if (forward) 1 else size - 1) % size
                it.copy(ratioIndex = next)
            }
        }
    }

    fun toggleHold() {
        viewModelScope.launch {
            val next = !drafts.draft.value.onHold
            drafts.edit { it.copy(onHold = next) }
            drafts.draft.value.bedId?.let { beds.setOnHold(it, next) }
        }
    }

    fun saveBed() {
        viewModelScope.launch {
            _state.update { it.copy(bloom = true) }
            val draft = drafts.draft.value
            val existing = draft.bedId?.let { beds.find(it) }
            val id = beds.store(draft.toBed(System.currentTimeMillis(), existing))
            drafts.edit { it.copy(bedId = id) }
            delay(GameConfig.SAVE_BLOOM_MS)
            _state.update { it.copy(bloom = false) }
            _events.send(BedLayoutEvent.Planned(id))
        }
    }

    private fun recompute(draft: BedDraft, config: AppSettings) {
        val catalogue = crops.all()
        val cropA = crops.byId(draft.primaryCropId) ?: catalogue.first()
        val cropB = draft.secondaryCropId?.let { crops.byId(it) }
        val layout = calculateLayout(draft, cropA, cropB)
        val animated = layout.total in 1..GameConfig.MAX_ANIMATED_PLANTS

        val badges = buildList {
            add(
                CropBadge(
                    id = cropA.id,
                    nameRes = cropA.nameRes,
                    spriteRes = familySprite(cropA.family),
                    tint = cropA.tint,
                    count = layout.totalA
                )
            )
            if (cropB != null) {
                add(
                    CropBadge(
                        id = cropB.id,
                        nameRes = cropB.nameRes,
                        spriteRes = familySprite(cropB.family),
                        tint = cropB.tint,
                        count = layout.totalB
                    )
                )
            }
        }

        val tooSmall = layout.total == 0
        _state.update {
            it.copy(
                ready = true,
                bedName = draft.name,
                lengthCm = draft.lengthCm,
                widthCm = draft.widthCm,
                lengthText = formatLength(draft.lengthCm, config.units),
                widthText = formatLength(draft.widthCm, config.units),
                imperial = config.units == UnitSystem.IMPERIAL,
                layout = layout,
                badges = badges,
                primaryId = cropA.id,
                primaryTint = cropA.tint,
                secondaryTint = cropB?.tint ?: cropA.tint,
                mix = cropB != null,
                ratioIndex = draft.ratioIndex,
                ratioText = draft.ratioA.toString() + RATIO_SEPARATOR + draft.ratioB.toString(),
                offsetRows = draft.offsetRows,
                onHold = draft.onHold,
                animated = animated,
                reveal = 0f,
                haptics = config.haptics,
                tooSmallCropNameRes = if (tooSmall) cropA.nameRes else null,
                tooSmallSpacing = cropA.spacingCm
            )
        }
        startReveal(animated)
    }

    private fun startReveal(animated: Boolean) {
        revealJob?.cancel()
        if (!animated) {
            _state.update { it.copy(reveal = 1f) }
            revealJob = null
            return
        }
        revealJob = viewModelScope.launch {
            for (step in 0..GameConfig.REVEAL_STEPS) {
                _state.update { it.copy(reveal = step / GameConfig.REVEAL_STEPS.toFloat()) }
                delay(GameConfig.REVEAL_STEP_MS)
            }
        }
    }

    private companion object {
        const val RATIO_SEPARATOR = " : "
    }
}
