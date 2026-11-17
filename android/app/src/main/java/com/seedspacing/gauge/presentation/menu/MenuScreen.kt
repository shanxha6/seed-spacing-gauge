package com.seedspacing.gauge.presentation.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedspacing.gauge.R
import com.seedspacing.gauge.core.di.ServiceLocator
import com.seedspacing.gauge.core.theme.LocalSeedColors
import com.seedspacing.gauge.core.ui.AppBackground
import com.seedspacing.gauge.core.ui.EmptyState
import com.seedspacing.gauge.core.ui.StatCard

@Composable
fun MenuScreen(
    onStartPlanning: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenBed: () -> Unit,
    onOpenNotes: (String) -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    val defaultName = stringResource(R.string.default_bed_name, state.nextIndex)

    MenuContent(
        state = state,
        onStartPlanning = {
            menuViewModel.startNewBed(defaultName)
            onStartPlanning()
        },
        onOpenGuide = onOpenGuide,
        onOpenSettings = onOpenSettings,
        onEditBed = { bedId ->
            menuViewModel.openBed(bedId)
            onOpenBed()
        },
        onOpenNotes = onOpenNotes
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onStartPlanning: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenSettings: () -> Unit,
    onEditBed: (String) -> Unit,
    onOpenNotes: (String) -> Unit
) {
    val colors = LocalSeedColors.current
    val hasBeds = state.beds.isNotEmpty()

    AppBackground(
        overlay = listOf(
            colors.paperTop.copy(alpha = 0.95f),
            colors.paperMid.copy(alpha = 0.91f),
            colors.paperDeep.copy(alpha = 0.94f)
        ),
        blobColor = colors.secondary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.menu_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary
                    )
                    Text(
                        text = stringResource(R.string.menu_subtitle),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
                IconButton(onClick = onOpenSettings, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.cd_settings),
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StartTile(
                    onClick = onStartPlanning,
                    modifier = Modifier
                        .weight(if (hasBeds) 2f else 3f)
                        .height(132.dp)
                )
                if (hasBeds) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(132.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            value = state.beds.size.toString(),
                            label = stringResource(R.string.stat_beds),
                            accent = colors.primary,
                            compact = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        StatCard(
                            value = state.totalPlants.toString(),
                            label = stringResource(R.string.stat_plants),
                            accent = colors.accentDark,
                            compact = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoTile(
                    title = stringResource(R.string.crop_guide),
                    value = null,
                    spriteRes = R.drawable.sprite_2,
                    onClick = onOpenGuide,
                    modifier = Modifier
                        .weight(1f)
                        .height(76.dp)
                )
                BentoTile(
                    title = if (hasBeds) {
                        stringResource(R.string.total_area)
                    } else {
                        stringResource(R.string.how_spacing_works)
                    },
                    value = if (hasBeds) {
                        stringResource(
                            if (state.imperial) R.string.area_imperial else R.string.area_metric,
                            state.areaText
                        )
                    } else {
                        null
                    },
                    spriteRes = R.drawable.sprite_5,
                    onClick = onOpenGuide,
                    modifier = Modifier
                        .weight(1f)
                        .height(76.dp)
                )
            }

            Text(
                text = stringResource(R.string.saved_beds),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary
            )

            if (hasBeds) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(state.beds, key = { it.id }) { bed ->
                        BedCard(
                            bed = bed,
                            imperial = state.imperial,
                            onEdit = { onEditBed(bed.id) },
                            onNotes = { onOpenNotes(bed.id) }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    EmptyState(
                        spriteRes = R.drawable.sprite_3,
                        title = stringResource(R.string.empty_beds_title),
                        message = stringResource(R.string.empty_beds_message)
                    )
                }
            }
        }
    }
}

@Composable
private fun StartTile(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalSeedColors.current
    val shape = RoundedCornerShape(18.dp)
    Button(
        onClick = onClick,
        shape = shape,
        elevation = null,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = colors.accentInk
        ),
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = colors.accent.copy(alpha = 0.28f),
                spotColor = colors.accent.copy(alpha = 0.28f)
            )
            .background(
                Brush.linearGradient(listOf(colors.accent, colors.accentDark)),
                shape
            )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 10.dp)
                    .size(56.dp)
                    .alpha(0.32f)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = stringResource(R.string.start_planning),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.accentInk
                )
            }
        }
    }
}

@Composable
private fun BentoTile(
    title: String,
    value: String?,
    spriteRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalSeedColors.current
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .background(colors.card.copy(alpha = 0.94f), shape)
            .border(1.dp, colors.secondary.copy(alpha = 0.8f), shape)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(spriteRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = 8.dp)
                .size(34.dp)
                .alpha(0.35f)
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (value != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.primary
                )
            }
        }
    }
}

@Composable
private fun BedCard(
    bed: BedSummary,
    imperial: Boolean,
    onEdit: () -> Unit,
    onNotes: () -> Unit
) {
    val colors = LocalSeedColors.current
    val shape = RoundedCornerShape(18.dp)
    val cropLine = if (bed.cropBName != null) {
        stringResource(R.string.crop_pair, stringResource(bed.cropAName), stringResource(bed.cropBName))
    } else {
        stringResource(bed.cropAName)
    }
    val dims = stringResource(
        if (imperial) R.string.bed_dims_imperial else R.string.bed_dims_metric,
        bed.lengthText,
        bed.widthText
    )
    val description = stringResource(R.string.cd_bed_card, bed.name, bed.plantCount)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .shadow(3.dp, shape, ambientColor = colors.primary.copy(alpha = 0.16f))
            .background(colors.card, shape)
            .border(1.dp, colors.secondary.copy(alpha = 0.8f), shape)
            .padding(horizontal = 12.dp)
            .semantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(bed.spriteRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(44.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = bed.name,
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.bed_card_meta, dims, cropLine),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (bed.onHold) {
                Text(
                    text = stringResource(R.string.on_hold),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.secondary
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = bed.plantCount.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = colors.primary
            )
            Text(
                text = stringResource(R.string.stat_plants),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary
            )
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onEdit, modifier = Modifier.size(width = 44.dp, height = 40.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = stringResource(R.string.cd_edit_bed),
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onNotes, modifier = Modifier.size(width = 44.dp, height = 40.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_note),
                    contentDescription = stringResource(R.string.cd_bed_notes),
                    tint = colors.accentDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(0.dp))
    }
}
