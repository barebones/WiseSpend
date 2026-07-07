package github.barebones.wisespend.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun WiseSpendTheme(
    content: @Composable () -> Unit
) {
    val mode = ThemeState.themeMode.value
    val scheme = ThemeState.colorScheme.value
    val isSystemDark = isSystemInDarkTheme()

    val isDark = when (mode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.AMOLED -> true
        AppThemeMode.SYSTEM -> isSystemDark
    }

    val colorScheme = resolveColorScheme(
        scheme = scheme,
        mode = mode,
        isSystemDark = isSystemDark
    )

    val view = LocalView.current

    // ── Fix system bars appearance ──
    LaunchedEffect(isDark, colorScheme.background) {
        val activity = view.context as? ComponentActivity ?: return@LaunchedEffect

        activity.enableEdgeToEdge(
            statusBarStyle = if (isDark) {
                SystemBarStyle.dark(
                    scrim = Color.Transparent.toArgb()
                )
            } else {
                SystemBarStyle.light(
                    scrim = Color.Transparent.toArgb(),
                    darkScrim = Color.Transparent.toArgb()
                )
            },
            navigationBarStyle = if (isDark) {
                SystemBarStyle.dark(
                    scrim = Color.Transparent.toArgb()
                )
            } else {
                SystemBarStyle.light(
                    scrim = Color.Transparent.toArgb(),
                    darkScrim = Color.Transparent.toArgb()
                )
            }
        )

        // Explicitly set status bar icon colors
        WindowCompat.getInsetsController(activity.window, view).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}