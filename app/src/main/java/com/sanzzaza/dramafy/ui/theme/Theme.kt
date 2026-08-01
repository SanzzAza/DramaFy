package com.sanzzaza.dramafy.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * DramaFy ships as a dark-first app. The system theme is consulted but only
 * honoured if [respectSystemTheme] is true. Pass false to always render dark.
 */
private val DarkColors = darkColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    primaryContainer = CrimsonDeep,
    onPrimaryContainer = Color.White,
    secondary = Magenta,
    onSecondary = Color.Black,
    tertiary = Amber,
    background = Night0,
    onBackground = TextHi,
    surface = Night1,
    onSurface = TextHi,
    surfaceVariant = Night2,
    onSurfaceVariant = TextMd,
    surfaceTint = Night3,
    outline = Hairline,
    outlineVariant = Night3,
    error = Color(0xFFFF6B6B),
    onError = Color.White
)

private val LightColors = lightColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E0),
    onPrimaryContainer = CrimsonDeep,
    secondary = Magenta,
    onSecondary = Color.Black,
    tertiary = Amber,
    background = Paper0,
    onBackground = InkHi,
    surface = Paper1,
    onSurface = InkHi,
    surfaceVariant = Paper2,
    onSurfaceVariant = InkMd,
    surfaceTint = Paper3,
    outline = Color(0xFFD5D7E0),
    outlineVariant = Color(0xFFE8EAF2),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun DramaFyTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            // Always light icons on dark background
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = DramaTypography,
        content = content
    )
}
