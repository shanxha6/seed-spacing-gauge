package com.seedspacing.gauge.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

private fun schemeFrom(colors: SeedColors, dark: Boolean) = if (dark) {
    darkColorScheme(
        primary = colors.primary,
        onPrimary = Color(0xFF1B1019),
        primaryContainer = colors.primaryDark,
        onPrimaryContainer = colors.textPrimary,
        secondary = colors.accent,
        onSecondary = colors.accentInk,
        tertiary = colors.secondary,
        onTertiary = colors.textPrimary,
        background = colors.paperMid,
        onBackground = colors.textPrimary,
        surface = colors.card,
        onSurface = colors.textPrimary,
        surfaceVariant = colors.cardAlt,
        onSurfaceVariant = colors.textSecondary,
        outline = colors.secondary,
        outlineVariant = colors.textMuted,
        error = colors.danger,
        onError = Color(0xFF1B1019)
    )
} else {
    lightColorScheme(
        primary = colors.primary,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = colors.primaryLight,
        onPrimaryContainer = colors.textPrimary,
        secondary = colors.accent,
        onSecondary = colors.accentInk,
        tertiary = colors.secondary,
        onTertiary = colors.textPrimary,
        background = colors.paperMid,
        onBackground = colors.textPrimary,
        surface = colors.card,
        onSurface = colors.textPrimary,
        surfaceVariant = colors.cardAlt,
        onSurfaceVariant = colors.textSecondary,
        outline = colors.secondary,
        outlineVariant = colors.textMuted,
        error = colors.danger,
        onError = Color(0xFFFFFFFF)
    )
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkSeedColors else LightSeedColors
    CompositionLocalProvider(LocalSeedColors provides colors) {
        MaterialTheme(
            colorScheme = schemeFrom(colors, darkTheme),
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
