package com.sandesh.wisespend.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MonoPrimaryDark,
    secondary = MonoSecondaryDark,
    tertiary = MonoTertiaryDark,
    background = MonoBackgroundDark,
    surface = MonoSurfaceDark,
    onPrimary = Color.Black,        // Dark text on bright primary
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,     // White text on dark background
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MonoPrimaryLight,
    secondary = MonoSecondaryLight,
    tertiary = MonoTertiaryLight,
    background = MonoBackgroundLight,
    surface = MonoSurfaceLight,
    onPrimary = Color.White,        // White text on dark primary
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1A1A1A), // Dark text on light background
    onSurface = Color(0xFF1A1A1A)
)

@Composable
fun WiseSpendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set default to false so your monochrome styling isn't ignored on Android 12+
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}