package com.seedspacing.gauge.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.seedspacing.gauge.core.theme.LocalSeedColors

data class ChipItem(val label: String, val iconRes: Int? = null)

@Composable
fun ChipRow(
    items: List<ChipItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedLabel: String,
    unselectedLabel: String
) {
    val colors = LocalSeedColors.current
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val active = index == selectedIndex
            val shape = RoundedCornerShape(20.dp)
            val state = if (active) selectedLabel else unselectedLabel
            Row(
                modifier = Modifier
                    .height(44.dp)
                    .defaultMinSize(minWidth = 64.dp)
                    .background(
                        if (active) colors.primary.copy(alpha = 0.14f) else colors.card.copy(alpha = 0.85f),
                        shape
                    )
                    .border(
                        if (active) 2.dp else 1.dp,
                        if (active) colors.primary else colors.secondary.copy(alpha = 0.8f),
                        shape
                    )
                    .clickable { onSelect(index) }
                    .padding(horizontal = 14.dp)
                    .semantics {
                        role = Role.Button
                        stateDescription = state
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (item.iconRes != null) {
                    Image(
                        painter = painterResource(item.iconRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (active) colors.primary else colors.textSecondary
                )
            }
        }
    }
}
