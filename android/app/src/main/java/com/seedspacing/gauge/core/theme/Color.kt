package com.seedspacing.gauge.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SeedColors(
    val paperTop: Color,
    val paperMid: Color,
    val paperDeep: Color,
    val card: Color,
    val cardAlt: Color,
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val accent: Color,
    val accentDark: Color,
    val accentInk: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val danger: Color,
    val kraft: Color,
    val soilTop: Color,
    val soilBottom: Color
)

val LightSeedColors = SeedColors(
    paperTop = Color(0xFFFDF8FC),
    paperMid = Color(0xFFF6EDF5),
    paperDeep = Color(0xFFEADAE6),
    card = Color(0xFFFFFFFF),
    cardAlt = Color(0xFFFBF2F8),
    primary = Color(0xFFAB2A9F),
    primaryDark = Color(0xFF7E1C76),
    primaryLight = Color(0xFFC457BA),
    accent = Color(0xFF28E091),
    accentDark = Color(0xFF12B873),
    accentInk = Color(0xFF0C3A26),
    secondary = Color(0xFFDDB0C3),
    textPrimary = Color(0xFF241823),
    textSecondary = Color(0xFF6B4E66),
    textMuted = Color(0xFF9B8696),
    danger = Color(0xFFD45A7E),
    kraft = Color(0xFFE8DDC4),
    soilTop = Color(0xFFFBF2F8),
    soilBottom = Color(0xFFF0DFEC)
)

val DarkSeedColors = SeedColors(
    paperTop = Color(0xFF241528),
    paperMid = Color(0xFF1B1019),
    paperDeep = Color(0xFF120A11),
    card = Color(0xFF2E1E2C),
    cardAlt = Color(0xFF3A2637),
    primary = Color(0xFFC457BA),
    primaryDark = Color(0xFF8E2186),
    primaryLight = Color(0xFFE189D9),
    accent = Color(0xFF3BEAA0),
    accentDark = Color(0xFF17A870),
    accentInk = Color(0xFF07231A),
    secondary = Color(0xFF8A6179),
    textPrimary = Color(0xFFF6EDF5),
    textSecondary = Color(0xFFDDB0C3),
    textMuted = Color(0xFF9B8696),
    danger = Color(0xFFE8769A),
    kraft = Color(0xFF4C4232),
    soilTop = Color(0xFF342030),
    soilBottom = Color(0xFF2A1826)
)

val LocalSeedColors = staticCompositionLocalOf { LightSeedColors }

val SplashOverlay = listOf(
    Color(0xE6471141),
    Color(0xF24A1B45),
    Color(0xF8241823)
)
