package com.rsalanga.roadmap.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Palette taken from the "Roadmap — AI Study Planner" Android (Material 3 Expressive) designs.
// Light values are the exact hexes used in the mockups; dark values are the matching
// tonal-palette steps (tone 80 / 20 / 30 / 90) for the same hues.

// Primary — teal
internal val Teal10 = Color(0xFF001F26)
internal val Teal20 = Color(0xFF00363F)
internal val Teal30 = Color(0xFF004E5C)
internal val Teal40 = Color(0xFF00687A)
internal val Teal80 = Color(0xFF84D2E6)
internal val Teal90 = Color(0xFFACEDFF)

// Secondary — muted teal
internal val TealGrey10 = Color(0xFF051F23)
internal val TealGrey20 = Color(0xFF1C3438)
internal val TealGrey30 = Color(0xFF334B4F)
internal val TealGrey40 = Color(0xFF4B6267)
internal val TealGrey80 = Color(0xFFB2CBD0)
internal val TealGrey90 = Color(0xFFCDE7EC)

// Tertiary — clay / study-session accent
internal val Clay10 = Color(0xFF351000)
internal val Clay20 = Color(0xFF5A1C00)
internal val Clay25 = Color(0xFF6B2E0F)
internal val Clay30 = Color(0xFF7A3412)
internal val Clay40 = Color(0xFF9A4520)
internal val Clay70 = Color(0xFFFFB596)
internal val Clay80 = Color(0xFFFFC3A8)
internal val Clay90 = Color(0xFFFFDBCA)

// Indigo — "busy / focus" calendar events
internal val Indigo10 = Color(0xFF121A37)
internal val Indigo30 = Color(0xFF3E4565)
internal val Indigo80 = Color(0xFFBEC5EB)
internal val Indigo90 = Color(0xFFDDE1FF)

// Error (M3 baseline)
internal val Red10 = Color(0xFF410002)
internal val Red20 = Color(0xFF690005)
internal val Red30 = Color(0xFF93000A)
internal val Red40 = Color(0xFFBA1A1A)
internal val Red80 = Color(0xFFFFB4AB)
internal val Red90 = Color(0xFFFFDAD6)

// Neutrals
internal val Neutral4 = Color(0xFF090F10)
internal val Neutral6 = Color(0xFF0E1415)
internal val Neutral10 = Color(0xFF171D1E)
internal val Neutral12 = Color(0xFF1B2122)
internal val Neutral17 = Color(0xFF252B2C)
internal val Neutral20 = Color(0xFF2B3133)
internal val Neutral22 = Color(0xFF303637)
internal val Neutral24 = Color(0xFF343A3B)
internal val Neutral87 = Color(0xFFD6DBDC)
internal val Neutral89 = Color(0xFFDEE3E5)
internal val Neutral91 = Color(0xFFE4E9EB)
internal val Neutral94 = Color(0xFFEAEFF0)
internal val Neutral95 = Color(0xFFECF2F3)
internal val Neutral96 = Color(0xFFF0F4F5)
internal val Neutral98 = Color(0xFFF6FAFB)

// Neutral variants
internal val NeutralVariant30 = Color(0xFF3F484B)
internal val NeutralVariant50 = Color(0xFF6F797B)
internal val NeutralVariant60 = Color(0xFF899295)
internal val NeutralVariant80 = Color(0xFFBFC8CB)
internal val NeutralVariant90 = Color(0xFFDBE4E6)

val RoadmapLightColorScheme: ColorScheme = lightColorScheme(
    primary = Teal40,
    onPrimary = Color.White,
    primaryContainer = Teal90,
    onPrimaryContainer = Teal10,
    inversePrimary = Teal80,
    secondary = TealGrey40,
    onSecondary = Color.White,
    secondaryContainer = TealGrey90,
    onSecondaryContainer = TealGrey10,
    tertiary = Clay40,
    onTertiary = Color.White,
    tertiaryContainer = Clay90,
    onTertiaryContainer = Clay10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Neutral98,
    onBackground = Neutral10,
    surface = Neutral98,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    surfaceTint = Teal40,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,
    scrim = Color.Black,
    surfaceBright = Neutral98,
    surfaceDim = Neutral87,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Neutral96,
    surfaceContainer = Neutral94,
    surfaceContainerHigh = Neutral91,
    surfaceContainerHighest = Neutral89
)

val RoadmapDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Teal80,
    onPrimary = Teal20,
    primaryContainer = Teal30,
    onPrimaryContainer = Teal90,
    inversePrimary = Teal40,
    secondary = TealGrey80,
    onSecondary = TealGrey20,
    secondaryContainer = TealGrey30,
    onSecondaryContainer = TealGrey90,
    tertiary = Clay70,
    onTertiary = Clay20,
    tertiaryContainer = Clay30,
    onTertiaryContainer = Clay90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Neutral6,
    onBackground = Neutral89,
    surface = Neutral6,
    onSurface = Neutral89,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    surfaceTint = Teal80,
    inverseSurface = Neutral89,
    inverseOnSurface = Neutral20,
    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,
    scrim = Color.Black,
    surfaceBright = Neutral24,
    surfaceDim = Neutral6,
    surfaceContainerLowest = Neutral4,
    surfaceContainerLow = Neutral10,
    surfaceContainer = Neutral12,
    surfaceContainerHigh = Neutral17,
    surfaceContainerHighest = Neutral22
)

/**
 * App-specific roles the design uses that have no slot in [ColorScheme]:
 * calendar event categories and the extra study-session (tertiary) shades.
 * Read them with `RoadmapTheme.extendedColors`.
 */
@Immutable
data class RoadmapExtendedColors(
    /** Secondary text on a `primaryContainer` card (e.g. "Nov 2 – Dec 27 · 8 weeks"). */
    val onPrimaryContainerVariant: Color,
    /** Secondary text on a `secondaryContainer` card (e.g. "Work · Meet"). */
    val onSecondaryContainerVariant: Color,
    /** "Busy / focus" calendar events. */
    val busyContainer: Color,
    val onBusyContainer: Color,
    val onBusyContainerVariant: Color,
    /** Overline label on a study card ("SPANISH · WEEK 2"). */
    val studyLabel: Color,
    /** Supporting text on a study card ("Session 6 of 40 · 45 min"). */
    val studySupporting: Color,
    /** Tonal button sitting on a study card ("Done"). */
    val studyTonal: Color,
    val onStudyTonal: Color,
    /** Decorative cookie shape behind a study card header. */
    val studyDecor: Color,
    /** Decorative cookie shape behind a primary card header. */
    val primaryDecor: Color,
    /** Decorative cookie shape on a `primary` surface (Coach tab hero). */
    val primaryDecorOnPrimary: Color
)

val RoadmapLightExtendedColors = RoadmapExtendedColors(
    onPrimaryContainerVariant = Teal30,
    onSecondaryContainerVariant = TealGrey30,
    busyContainer = Indigo90,
    onBusyContainer = Indigo10,
    onBusyContainerVariant = Indigo30,
    studyLabel = Clay30,
    studySupporting = Clay25,
    studyTonal = Clay70,
    onStudyTonal = Clay10,
    studyDecor = Clay80,
    primaryDecor = Color(0xFF7FD4EA),
    primaryDecorOnPrimary = Color(0xFF1A7D8F)
)

val RoadmapDarkExtendedColors = RoadmapExtendedColors(
    onPrimaryContainerVariant = Teal80,
    onSecondaryContainerVariant = TealGrey80,
    busyContainer = Indigo30,
    onBusyContainer = Indigo90,
    onBusyContainerVariant = Indigo80,
    studyLabel = Clay70,
    studySupporting = Clay80,
    studyTonal = Clay40,
    onStudyTonal = Color.White,
    studyDecor = Clay25,
    primaryDecor = Color(0xFF1A6878),
    primaryDecorOnPrimary = Color(0xFF6FC4DA)
)

val LocalRoadmapExtendedColors = staticCompositionLocalOf { RoadmapLightExtendedColors }
