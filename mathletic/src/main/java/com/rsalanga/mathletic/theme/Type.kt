package com.rsalanga.mathletic.theme

import androidx.compose.material3.Typography
import com.rsalanga.mathletic.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// Fonts (both free on Google Fonts, OFL licence). Put these in res/font/:
//   anton_regular.ttf
//   figtree_medium.ttf, figtree_semibold.ttf, figtree_bold.ttf, figtree_extrabold.ttf
// ---------------------------------------------------------------------------
val Anton = FontFamily(Font(R.font.anton_regular, FontWeight.Normal))

val Figtree = FontFamily(
    Font(R.font.figtree_medium, FontWeight.Medium),
    Font(R.font.figtree_semibold, FontWeight.SemiBold),
    Font(R.font.figtree_bold, FontWeight.Bold),
    Font(R.font.figtree_extrabold, FontWeight.ExtraBold),
)

// Anton = big condensed display (hero titles, question prompts, mode names).
// Figtree = everything else. Display/headline text is usually shown UPPERCASE
// in the designs — apply .uppercase() at the call site, not in the style.
val MathleticTypography = Typography(
    displayLarge = TextStyle(fontFamily = Anton, fontSize = 64.sp, lineHeight = 60.sp),
    displayMedium = TextStyle(fontFamily = Anton, fontSize = 44.sp, lineHeight = 42.sp),
    displaySmall = TextStyle(fontFamily = Anton, fontSize = 38.sp, lineHeight = 36.sp),

    headlineLarge = TextStyle(fontFamily = Anton, fontSize = 32.sp, lineHeight = 32.sp),
    headlineMedium = TextStyle(fontFamily = Anton, fontSize = 28.sp, lineHeight = 28.sp),
    headlineSmall = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.01).em),

    titleLarge = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, lineHeight = 20.sp),

    bodyLarge = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 23.sp),
    bodyMedium = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp),

    labelLarge = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.1.em),
)

/**
 * The question prompt shrinks with its length so long skills
 * ("1h 50m + 1h 25m") still fit on one line. Same steps as the designs.
 */
fun promptStyle(prompt: String): TextStyle {
    val size = when (prompt.length) {
        in 0..4 -> 96
        in 5..7 -> 80
        in 8..10 -> 62
        in 11..13 -> 50
        else -> 42
    }
    return TextStyle(fontFamily = Anton, fontSize = size.sp, lineHeight = size.sp)
}

/** Big numbers in HUDs (timer, score, streak) — tabular so digits don’t jump. */
val NumberStyle = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.ExtraBold,
    fontFeatureSettings = "tnum",
)
