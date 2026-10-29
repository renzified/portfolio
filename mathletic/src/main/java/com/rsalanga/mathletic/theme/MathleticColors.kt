package com.rsalanga.mathletic.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Game-specific tokens that Material 3 has no role for.
// Usage:  MaterialTheme.mathleticColors.mode(GameMode.Blitz).accent
//         MaterialTheme.mathleticColors.category(SkillCategory.Percentages)
// ---------------------------------------------------------------------------

enum class GameMode { Sprint, Blitz, Survival, Practice }

enum class SkillCategory { Arithmetic, Percentages, Fractions, PowersRoots, NumberSense, Everyday }

@Immutable
data class ModeColors(
    val accent: Color, // icon tile, Start button, HUD highlights
    val onAccent: Color,
    val container: Color, // HUD card, rules panel
    val onContainer: Color
)

@Immutable
data class MathleticColors(
    val sprint: ModeColors,
    val blitz: ModeColors,
    val survival: ModeColors,
    val practice: ModeColors,
    val categories: Map<SkillCategory, Color>,
    val correct: Color, // question card flash
    // picked choice that was right
    val correctStrong: Color,
    val incorrect: Color,
    val incorrectStrong: Color,
    val onFeedback: Color,
    // progress rings and bars
    val mastery: Color,
    // Survival lives
    val heart: Color
) {
    fun mode(mode: GameMode): ModeColors = when (mode) {
        GameMode.Sprint -> sprint
        GameMode.Blitz -> blitz
        GameMode.Survival -> survival
        GameMode.Practice -> practice
    }

    fun category(category: SkillCategory): Color = categories.getValue(category)
}

private val Ink = Color(0xFF1B1410)

val LightMathleticColors = MathleticColors(
    sprint = ModeColors(Color(0xFFFF7A2F), Ink, Color(0xFFFFE1C7), Color(0xFF3B1D08)),
    blitz = ModeColors(Color(0xFFF5B700), Ink, Color(0xFFFFEFB8), Color(0xFF3B2A00)),
    survival = ModeColors(Color(0xFFF0577A), Ink, Color(0xFFFFD9DF), Color(0xFF6B2A38)),
    practice = ModeColors(Color(0xFF8E70FF), Ink, Color(0xFFE7DEFF), Color(0xFF2A1A66)),
    categories = mapOf(
        SkillCategory.Arithmetic to Color(0xFFFFE1C7),
        SkillCategory.Percentages to Color(0xFFE7DEFF),
        SkillCategory.Fractions to Color(0xFFD8ECFF),
        SkillCategory.PowersRoots to Color(0xFFFFEFB8),
        SkillCategory.NumberSense to Color(0xFFDDF3E5),
        SkillCategory.Everyday to Color(0xFFFFD9DF)
    ),
    correct = Color(0xFFDDF3E5),
    correctStrong = Color(0xFF9BE0B4),
    incorrect = Color(0xFFFFE0E0),
    incorrectStrong = Color(0xFFFFB3B3),
    onFeedback = Ink,
    mastery = Color(0xFF26784C),
    heart = Color(0xFFF0577A)
)

val DarkMathleticColors = MathleticColors(
    sprint = ModeColors(Color(0xFFFF8F4F), Color(0xFF2A1204), Color(0xFF5A2A0E), Color(0xFFFFDCC4)),
    blitz = ModeColors(Color(0xFFFFC933), Color(0xFF2B1F00), Color(0xFF4D3A00), Color(0xFFFFEFB8)),
    survival = ModeColors(
        Color(0xFFFF7D99),
        Color(0xFF3D0715),
        Color(0xFF5C1F2E),
        Color(0xFFFFD9DF)
    ),
    practice = ModeColors(
        Color(0xFFB9A6FF),
        Color(0xFF23135C),
        Color(0xFF3E2D8A),
        Color(0xFFE7DEFF)
    ),
    categories = mapOf(
        SkillCategory.Arithmetic to Color(0xFF4A2E1A),
        SkillCategory.Percentages to Color(0xFF352B5C),
        SkillCategory.Fractions to Color(0xFF1E3550),
        SkillCategory.PowersRoots to Color(0xFF4A3D10),
        SkillCategory.NumberSense to Color(0xFF1E3D2B),
        SkillCategory.Everyday to Color(0xFF4D2530)
    ),
    correct = Color(0xFF1E3D2B),
    correctStrong = Color(0xFF2F6B47),
    incorrect = Color(0xFF4D2020),
    incorrectStrong = Color(0xFF7A2E2E),
    onFeedback = Color(0xFFF2E6DA),
    mastery = Color(0xFF7DD3A0),
    heart = Color(0xFFFF7D99)
)

val LocalMathleticColors = staticCompositionLocalOf { LightMathleticColors }

/** Read game tokens next to the normal M3 ones: MaterialTheme.mathleticColors */
val MaterialTheme.mathleticColors: MathleticColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMathleticColors.current
