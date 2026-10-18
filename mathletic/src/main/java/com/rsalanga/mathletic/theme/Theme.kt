package com.rsalanga.mathletic.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

/**
 * Mathletic theme: Material 3 Expressive with the brand palette.
 *
 * - dynamicColor is OFF by default. Wallpaper colours would replace the brand
 *   orange, and the game modes rely on fixed colours (Sprint orange, Blitz
 *   yellow…). If you add it as a setting, game tokens (mathleticColors) stay
 *   fixed anyway.
 * - MotionScheme.expressive() gives the springy, overshooting motion used by
 *   button groups, the FAB menu, sheets, etc.
 */
@Composable
fun MathleticTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    val mathleticColors = if (darkTheme) DarkMathleticColors else LightMathleticColors

    CompositionLocalProvider(LocalMathleticColors provides mathleticColors) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = MotionScheme.expressive(),
            shapes = MathleticShapes,
            typography = MathleticTypography,
            content = content,
        )
    }
}
