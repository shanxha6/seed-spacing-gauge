package com.seedspacing.gauge.presentation.notes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedspacing.gauge.R
import com.seedspacing.gauge.core.config.GameConfig
import com.seedspacing.gauge.core.di.ServiceLocator
import com.seedspacing.gauge.core.theme.LocalSeedColors
import com.seedspacing.gauge.core.ui.AppBackground
import com.seedspacing.gauge.core.ui.EmptyState
import com.seedspacing.gauge.core.ui.PrimaryButton
import com.seedspacing.gauge.core.ui.ScreenHeader
import com.seedspacing.gauge.core.ui.SeedPacketCard

@Composable
fun SowingNotesScreen(bedId: String, onBack: () -> Unit) {
    val notesViewModel: SowingNotesViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by notesViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(bedId) {
        notesViewModel.bind(bedId)
    }

    SowingNotesContent(
        state = state,
        onBack = onBack,
        onDraftChange = { notesViewModel.updateDraft(it) },
        onCommit = { notesViewModel.commitNote() },
        onDelete = { notesViewModel.deleteNote(it) }
    )
}

@Composable
private fun SowingNotesContent(
    state: SowingNotesUiState,
    onBack: () -> Unit,
    onDraftChange: (String) -> Unit,
    onCommit: () -> Unit,
    onDelete: (String) -> Unit
) {
    val colors = LocalSeedColors.current

    AppBackground(
        overlay = listOf(
            colors.paperTop.copy(alpha = 0.95f),
            colors.paperMid.copy(alpha = 0.93f),
            colors.paperDeep.copy(alpha = 0.95f)
        ),
        blobColor = colors.secondary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.notes_title),
                subtitle = state.bedName,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(10.dp))

            SeedPacketCard(modifier = Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (state.spriteRes != null) {
                        Image(
                            painter = painterResource(state.spriteRes),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = if (state.cropNameRes != null) stringResource(state.cropNameRes) else state.bedName,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(R.string.legend_count, state.plantCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.draftText,
                    onValueChange = onDraftChange,
                    shape = RoundedCornerShape(14.dp),
                    minLines = 4,
                    placeholder = { Text(text = stringResource(R.string.note_placeholder)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(
                        R.string.note_counter,
                        state.draftText.length,
                        GameConfig.MAX_NOTE_CHARS
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                PrimaryButton(
                    text = stringResource(R.string.save_note),
                    onClick = onCommit,
                    enabled = state.canSave,
                    height = 52.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (state.notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    EmptyState(
                        spriteRes = R.drawable.sprite_4,
                        title = stringResource(R.string.notes_empty),
                        message = stringResource(R.string.notes_empty_message),
                        spriteSize = 64.dp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(state.notes, key = { it.id }) { note ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 72.dp)
                                .background(colors.card, RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = note.dateText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = note.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textPrimary,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = { onDelete(note.id) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete),
                                    contentDescription = stringResource(R.string.cd_delete_note),
                                    tint = colors.danger,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
