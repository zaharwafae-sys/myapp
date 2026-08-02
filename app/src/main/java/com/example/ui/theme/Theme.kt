package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SophisticatedPrimary,
    onPrimary = SophisticatedOnPrimary,
    primaryContainer = SophisticatedPrimaryContainer,
    onPrimaryContainer = SophisticatedOnPrimaryContainer,
    secondary = SophisticatedPrimaryContainer,
    onSecondary = SophisticatedOnPrimaryContainer,
    background = SophisticatedDarkBackground,
    onBackground = SophisticatedText,
    surface = SophisticatedDarkSurface,
    onSurface = SophisticatedText,
    surfaceVariant = SophisticatedDarkContainer,
    onSurfaceVariant = SophisticatedTextMuted,
    tertiary = AccentGold
)

private val LightColorScheme = lightColorScheme(
    primary = SophisticatedLightPrimary,
    onPrimary = SophisticatedLightOnPrimary,
    primaryContainer = SophisticatedLightPrimaryContainer,
    onPrimaryContainer = SophisticatedLightOnPrimaryContainer,
    secondary = SophisticatedLightPrimary,
    onSecondary = SophisticatedLightOnPrimary,
    background = SophisticatedLightBackground,
    onBackground = SophisticatedLightText,
    surface = SophisticatedLightSurface,
    onSurface = SophisticatedLightText,
    surfaceVariant = SophisticatedLightContainer,
    onSurfaceVariant = SophisticatedLightTextMuted,
    tertiary = AccentPurple
)

@Composable
fun AdamBarcodeMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
