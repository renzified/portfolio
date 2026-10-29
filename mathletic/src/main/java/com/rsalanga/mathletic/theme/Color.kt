package com.rsalanga.mathletic.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Brand palette — the raw values used in the Mathletic redesign.
// Screens should NOT use these directly; read roles from MaterialTheme.colorScheme
// or game tokens from MaterialTheme.mathleticColors instead.
// ---------------------------------------------------------------------------
object Brand {
    val Ink = Color(0xFF1B1410) // text, dark buttons, active nav pill
    val Cream = Color(0xFFFBF5EC) // app background
    val Sand = Color(0xFFF3E7D8) // tonal buttons, keypad keys, nav bar
    val Orange = Color(0xFFFF7A2F) // primary / Sprint
    val Peach = Color(0xFFFFE1C7)
    val Purple = Color(0xFF8E70FF) // secondary / Practice
    val Lavender = Color(0xFFE7DEFF)
    val Green = Color(0xFF26784C) // mastery / correct
    val Mint = Color(0xFFDDF3E5)
}

// ---------------------------------------------------------------------------
// Light scheme (the main look). Orange fills carry INK text, not white:
// white on #FF7A2F fails WCAG contrast; ink on it is ~7:1.
// ---------------------------------------------------------------------------
val LightColors = lightColorScheme(
    primary = Color(0xFFFF7A2F),
    onPrimary = Color(0xFF1B1410),
    primaryContainer = Color(0xFFFFE1C7),
    onPrimaryContainer = Color(0xFF3B1D08),
    inversePrimary = Color(0xFFFFB68A),

    secondary = Color(0xFF8E70FF),
    onSecondary = Color(0xFF1B1410),
    secondaryContainer = Color(0xFFE7DEFF),
    onSecondaryContainer = Color(0xFF2A1A66),

    tertiary = Color(0xFF26784C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDDF3E5),
    onTertiaryContainer = Color(0xFF1E5B3A),

    error = Color(0xFFB42318),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFE0E0),
    onErrorContainer = Color(0xFF8A1C1C),

    background = Color(0xFFFBF5EC),
    onBackground = Color(0xFF1B1410),
    surface = Color(0xFFFBF5EC),
    onSurface = Color(0xFF1B1410),
    surfaceVariant = Color(0xFFF3E7D8),
    onSurfaceVariant = Color(0xFF5C4636),
    surfaceTint = Color(0xFFFF7A2F),

    surfaceDim = Color(0xFFE8DCCD),
    surfaceBright = Color(0xFFFBF5EC),
    surfaceContainerLowest = Color(0xFFFFFFFF), // cards, question card
    surfaceContainerLow = Color(0xFFFFF9F2),
    surfaceContainer = Color(0xFFF3E7D8), // nav bar, tonal buttons, keys
    surfaceContainerHigh = Color(0xFFEADCCB),
    surfaceContainerHighest = Color(0xFFE3D3C1),

    inverseSurface = Color(0xFF1B1410), // active nav pill, dark CTA
    inverseOnSurface = Color(0xFFFFF5EB),

    outline = Color(0xFF8A7868),
    outlineVariant = Color(0xFFCDBBA8),
    scrim = Color(0xFF000000)
)

// ---------------------------------------------------------------------------
// Dark scheme — warm charcoal, same hues lifted for contrast.
// ---------------------------------------------------------------------------
val DarkColors = darkColorScheme(
    primary = Color(0xFFFF8F4F),
    onPrimary = Color(0xFF2A1204),
    primaryContainer = Color(0xFF6B2E08),
    onPrimaryContainer = Color(0xFFFFDCC4),
    inversePrimary = Color(0xFF9A3A00),

    secondary = Color(0xFFB9A6FF),
    onSecondary = Color(0xFF23135C),
    secondaryContainer = Color(0xFF3E2D8A),
    onSecondaryContainer = Color(0xFFE7DEFF),

    tertiary = Color(0xFF7DD3A0),
    onTertiary = Color(0xFF003920),
    tertiaryContainer = Color(0xFF1E5B3A),
    onTertiaryContainer = Color(0xFFDDF3E5),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF17110D),
    onBackground = Color(0xFFF2E6DA),
    surface = Color(0xFF17110D),
    onSurface = Color(0xFFF2E6DA),
    surfaceVariant = Color(0xFF52443A),
    onSurfaceVariant = Color(0xFFD6C3B4),
    surfaceTint = Color(0xFFFF8F4F),

    surfaceDim = Color(0xFF17110D),
    surfaceBright = Color(0xFF3F3530),
    surfaceContainerLowest = Color(0xFF120C09),
    surfaceContainerLow = Color(0xFF1F1813),
    surfaceContainer = Color(0xFF241C17),
    surfaceContainerHigh = Color(0xFF2F2621),
    surfaceContainerHighest = Color(0xFF3A302A),

    inverseSurface = Color(0xFFF2E6DA),
    inverseOnSurface = Color(0xFF2E2520),

    outline = Color(0xFF9F8D7F),
    outlineVariant = Color(0xFF52443A),
    scrim = Color(0xFF000000)
)
