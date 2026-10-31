package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Plum,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnPrimary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = OliveTertiary,
    onTertiary = OnPrimary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Canvas,
    onBackground = Ink,
    surface = Surface,
    onSurface = Ink,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = InkMuted,
    outline = Border,
    outlineVariant = OutlineVariant,
    error = ErrorColor
)

private val DarkColorScheme = darkColorScheme(
    primary = InversePrimary,
    onPrimary = Ink,
    primaryContainer = PlumPrimary,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SoftRose,
    onSecondary = Ink,
    secondaryContainer = Secondary,
    onSecondaryContainer = Surface,
    tertiary = OliveTertiary,
    onTertiary = OnPrimary,
    background = Ink,
    onBackground = SurfaceBright,
    surface = Color(0xFF312A2D),
    onSurface = SurfaceBright,
    surfaceVariant = Color(0xFF3F353A),
    onSurfaceVariant = Border,
    outline = Color(0xFF5A4D52),
    outlineVariant = Color(0xFF4A3E43),
    error = ErrorColor
)

@Composable
fun AtelierBeautyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Warm editorial studio maintains curated warm aesthetics across light/dark
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
