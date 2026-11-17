package com.seedspacing.gauge.presentation.guide

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seedspacing.gauge.R
import com.seedspacing.gauge.core.di.ServiceLocator
import com.seedspacing.gauge.core.theme.LocalSeedColors
import com.seedspacing.gauge.core.ui.AppBackground
import com.seedspacing.gauge.core.ui.ChipItem
import com.seedspacing.gauge.core.ui.ChipRow
import com.seedspacing.gauge.core.ui.ScreenHeader
import com.seedspacing.gauge.core.ui.SeedPacketCard
import com.seedspacing.gauge.core.ui.familyLabel
import com.seedspacing.gauge.core.ui.familySprite
import com.seedspacing.gauge.domain.model.CropFamily

@Composable
fun CropGuideScreen(onBack: () -> Unit) {
    val guideViewModel: CropGuideViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by guideViewModel.state.collectAsStateWithLifecycle()

    CropGuideContent(
        state = state,
        onBack = onBack,
        onFamily = { guideViewModel.selectFamily(it) }
    )
}

@Composable
private fun CropGuideContent(
    state: CropGuideUiState,
    onBack: () -> Unit,
    onFamily: (Int) -> Unit
) {
    val colors = LocalSeedColors.current
    val unitLabel = stringResource(if (state.imperial) R.string.unit_inch_short else R.string.unit_cm_short)
    val familyChips = CropFamily.entries.map {
        ChipItem(stringResource(familyLabel(it)), familySprite(it))
    }

    AppBackground(
        overlay = listOf(
            colors.paperTop.copy(alpha = 0.96f),
            colors.paperMid.copy(alpha = 0.93f),
            colors.paperDeep.copy(alpha = 0.95f)
        ),
        blobColor = colors.primary,
        blobAlpha = 0.08f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.guide_title),
                subtitle = stringResource(R.string.guide_subtitle),
                onBack = onBack
            )

            ChipRow(
                items = familyChips,
                selectedIndex = state.familyIndex,
                onSelect = onFamily,
                selectedLabel = stringResource(R.string.state_selected),
                unselectedLabel = stringResource(R.string.state_not_selected),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.col_crop),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(2.4f)
                )
                Text(
                    text = stringResource(R.string.col_spacing),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.col_depth),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.col_days),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.9f)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                itemsIndexed(state.rows, key = { _, row -> row.id }) { index, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp)
                            .background(
                                if (index % 2 == 0) colors.card else colors.cardAlt,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.weight(2.4f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(row.spriteRes),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = stringResource(row.nameRes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = stringResource(R.string.value_with_unit, row.spacingText, unitLabel),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(row.tint),
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = stringResource(R.string.value_with_unit, row.depthText, unitLabel),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = row.days.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            SeedPacketCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                padding = 14.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = stringResource(R.string.guide_footer),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
