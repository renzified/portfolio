package com.rsalanga.roadmap.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.rsalanga.roadmap.R

// Fonts from the design: Fraunces (soft serif) for headings and large numbers,
// Manrope (geometric sans) for everything else. Both are bundled as variable fonts:
//   res/font/fraunces.ttf  ← Fraunces[SOFT,WONK,opsz,wght].ttf
//   res/font/manrope.ttf   ← Manrope[wght].ttf
// (Google Fonts, SIL Open Font License.)

@OptIn(ExperimentalTextApi::class)
private fun fraunces(weight: Int) = Font(
    resId = R.font.fraunces,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(
        FontVariation.weight(weight),
        FontVariation.Setting("opsz", 72f), // display optical size
        FontVariation.Setting("SOFT", 50f), // softened serifs, as in the design
    ),
)

@OptIn(ExperimentalTextApi::class)
private fun manrope(weight: Int) = Font(
    resId = R.font.manrope,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

/** Headings, hero text, large numbers. */
val RoadmapDisplayFontFamily: FontFamily = FontFamily(
    fraunces(400),
    fraunces(500),
    fraunces(600),
    fraunces(700),
)

/** Body, labels, buttons, navigation. */
val RoadmapFontFamily: FontFamily = FontFamily(
    manrope(300),
    manrope(400),
    manrope(500),
    manrope(600),
    manrope(700),
    manrope(800),
)

private fun style(
    family: FontFamily,
    size: Int,
    lineHeight: Int,
    weight: Int,
    letterSpacing: Double = 0.0,
) = TextStyle(
    fontFamily = family,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    fontWeight = FontWeight(weight),
    letterSpacing = letterSpacing.sp,
)

private fun display(size: Int, lineHeight: Int, weight: Int, letterSpacing: Double = 0.0) =
    style(RoadmapDisplayFontFamily, size, lineHeight, weight, letterSpacing)

private fun text(size: Int, lineHeight: Int, weight: Int, letterSpacing: Double = 0.0) =
    style(RoadmapFontFamily, size, lineHeight, weight, letterSpacing)

val RoadmapTypography = Typography(
    displayLarge = display(57, 64, 500, -0.25),
    displayMedium = display(45, 52, 500),
    // Hero headings — "What do you want to learn?"
    displaySmall = display(36, 42, 600, -0.2),
    // Screen titles — "October", "Plans", "Settings"
    headlineLarge = display(34, 40, 600, -0.2),
    // Detail hero — "Past tense drills"
    headlineMedium = display(32, 38, 600, -0.2),
    // Featured card title — "Conversational Spanish"
    headlineSmall = display(24, 28, 600),
    // Section / top-bar title — "Today", "Coach"
    titleLarge = display(22, 28, 500),
    // Card / question title — "How much can you commit each week?"
    titleMedium = text(17, 23, 600),
    // List-item title
    titleSmall = text(15, 20, 600),
    bodyLarge = text(16, 22, 400),
    // Chat bubbles, descriptions
    bodyMedium = text(15, 21, 400),
    // Supporting text
    bodySmall = text(13, 18, 400),
    // Buttons, chips
    labelLarge = text(14, 20, 600),
    // Overlines — "STEP 1 OF 5 · SKILL", nav labels
    labelMedium = text(12, 16, 600, 0.3),
    labelSmall = text(11, 16, 500),
)

/** Overline used on study cards ("SPANISH · WEEK 2"). */
val RoadmapOverline: TextStyle = text(12, 16, 700).copy(letterSpacing = 0.04.em)

/** Large numerals — stats ("4 / 7"), progress ("72%"), week-strip dates. */
val RoadmapNumeral: TextStyle = display(22, 28, 600)
