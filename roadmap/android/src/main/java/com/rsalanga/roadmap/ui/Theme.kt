package com.rsalanga.roadmap.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

/**
 * Material 3 Expressive theme for the Roadmap app.
 *
 * @param darkTheme follow the system setting by default.
 * @param dynamicColor use the wallpaper-based scheme on Android 12+. Off by default so the
 * brand teal/clay palette from the designs is shown.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RoadmapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> RoadmapDarkColorScheme
        else -> RoadmapLightColorScheme
    }
    val extendedColors = if (darkTheme) RoadmapDarkExtendedColors else RoadmapLightExtendedColors

    CompositionLocalProvider(LocalRoadmapExtendedColors provides extendedColors) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = MotionScheme.expressive(),
            shapes = RoadmapShapes,
            typography = RoadmapTypography,
            content = content,
        )
    }
}

/** Accessors for theme values that live outside [MaterialTheme]. */
object RoadmapTheme {
    val extendedColors: RoadmapExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalRoadmapExtendedColors.current
}
