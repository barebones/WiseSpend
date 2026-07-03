package com.sandesh.wisespend.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
    LIGHT, DARK, AMOLED, SYSTEM
}

enum class AppColorScheme(val label: String) {
//    MONO("Mono"),
    MONO2("Mono"),
    OCEAN("Ocean"),
    FOREST("Forest"),
    SUNSET("Sunset"),
    LAVENDER("Lavender"),
    ROSE("Rosé"),
    EMBER("Ember")
}

data class SchemeColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val surface: Color,
    val background: Color,
    val onPrimary: Color = Color.White,
    val onBackground: Color = Color(0xFF1A1A1A),
    val onSurface: Color = Color(0xFF1A1A1A)
)


private val MonoLight = SchemeColors(
    primary = Color(0xFF171717),
    secondary = Color(0xFF404040),
    tertiary = Color(0xFF737373),
    surface = Color(0xFFFAFAFA),
    background = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFAFAFA),
    onBackground = Color(0xFF0A0A0A),
    onSurface = Color(0xFF171717)
)

private val MonoLight2 = SchemeColors(
    background = Color(0xFFF1F1F3),
    surface = Color(0xFFFFFFFF),
    primary = Color(0xFF000000),
    secondary = Color(0xFFE4E4E7),

    // Text & Icon Colors
    onBackground = Color(0xFF0F0F10),
    onSurface = Color(0xFF3F3F46),
    onPrimary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF71717A)
)

private val OceanLight = SchemeColors(
    primary = Color(0xFF0066CC),
    secondary = Color(0xFF3399FF),
    tertiary = Color(0xFF66BBFF),
    surface = Color(0xFFF0F7FF),
    background = Color(0xFFFAFCFF)
)

private val ForestLight = SchemeColors(
    primary = Color(0xFF2E7D32),
    secondary = Color(0xFF4CAF50),
    tertiary = Color(0xFF81C784),
    surface = Color(0xFFF1F8E9),
    background = Color(0xFFFAFDF6)
)

private val SunsetLight = SchemeColors(
    primary = Color(0xFFE65100),
    secondary = Color(0xFFFF8A65),
    tertiary = Color(0xFFFFAB91),
    surface = Color(0xFFFFF3E0),
    background = Color(0xFFFFFBF5)
)

private val LavenderLight = SchemeColors(
    primary = Color(0xFF6A4FC7),
    secondary = Color(0xFF9575CD),
    tertiary = Color(0xFFB39DDB),
    surface = Color(0xFFF3EFFF),
    background = Color(0xFFFAF8FF)
)

private val RoseLight = SchemeColors(
    primary = Color(0xFFC2185B),
    secondary = Color(0xFFE91E63),
    tertiary = Color(0xFFF48FB1),
    surface = Color(0xFFFFF0F5),
    background = Color(0xFFFFFAFC)
)

private val EmberLight = SchemeColors(
    primary = Color(0xFFD84315),
    secondary = Color(0xFFFF7043),
    tertiary = Color(0xFFFF8A65),
    surface = Color(0xFFFBE9E7),
    background = Color(0xFFFFF8F6)
)

private val MonoDark = SchemeColors(
    primary = Color(0xFFFAFAFA),
    secondary = Color(0xFFD4D4D4),
    tertiary = Color(0xFF8A8A8A),
    surface = Color(0xFF171717),
    background = Color(0xFF000000),
    onPrimary = Color(0xFF0A0A0A),
    onBackground = Color(0xFFFAFAFA),
    onSurface = Color(0xFFFAFAFA)
)

private val MonoDark2 = SchemeColors(
    background = Color(0xFF0C0C0E),
    surface = Color(0xFF1E1E22),
    primary = Color(0xFFFFFFFF),
    secondary = Color(0xFF2D2D34),
    onBackground = Color(0xFFFAFAFA),
    onSurface = Color(0xFFD4D4D8),
    onPrimary = Color(0xFF0C0C0E),
    tertiary = Color(0xFF71717A)
)

private val OceanDark = SchemeColors(
    primary = Color(0xFF82B1FF),
    secondary = Color(0xFF448AFF),
    tertiary = Color(0xFF2979FF),
    surface = Color(0xFF1A2332),
    background = Color(0xFF05080C),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val ForestDark = SchemeColors(
    primary = Color(0xFF81C784),
    secondary = Color(0xFF66BB6A),
    tertiary = Color(0xFF4CAF50),
    surface = Color(0xFF1B2E1B),
    background = Color(0xFF091309),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val SunsetDark = SchemeColors(
    primary = Color(0xFFFFAB91),
    secondary = Color(0xFFFF8A65),
    tertiary = Color(0xFFFF7043),
    surface = Color(0xFF2E1E14),
    background = Color(0xFF1A110A),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LavenderDark = SchemeColors(
    primary = Color(0xFFB39DDB),
    secondary = Color(0xFF9575CD),
    tertiary = Color(0xFF7E57C2),
    surface = Color(0xFF231E33),
    background = Color(0xFF15112A),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val RoseDark = SchemeColors(
    primary = Color(0xFFF48FB1),
    secondary = Color(0xFFE91E63),
    tertiary = Color(0xFFC2185B),
    surface = Color(0xFF2E1520),
    background = Color(0xFF130A0F),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val EmberDark = SchemeColors(
    primary = Color(0xFFFF8A65),
    secondary = Color(0xFFFF7043),
    tertiary = Color(0xFFF4511E),
    surface = Color(0xFF2E1A14),
    background = Color(0xFF150D08),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)


private fun SchemeColors.toAmoled() = copy(
    surface = Color(0xFF151515),
    background = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)


fun resolveColorScheme(
    scheme: AppColorScheme,
    mode: AppThemeMode,
    isSystemDark: Boolean
): androidx.compose.material3.ColorScheme {
    val isDark = when (mode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.AMOLED -> true
        AppThemeMode.SYSTEM -> isSystemDark
    }
    val isAmoled = mode == AppThemeMode.AMOLED

    val colors = when (scheme) {
//        AppColorScheme.MONO -> if (isDark) MonoDark else MonoLight
        AppColorScheme.MONO2 -> if (isDark) MonoDark2 else MonoLight2
        AppColorScheme.OCEAN -> if (isDark) OceanDark else OceanLight
        AppColorScheme.FOREST -> if (isDark) ForestDark else ForestLight
        AppColorScheme.SUNSET -> if (isDark) SunsetDark else SunsetLight
        AppColorScheme.LAVENDER -> if (isDark) LavenderDark else LavenderLight
        AppColorScheme.ROSE -> if (isDark) RoseDark else RoseLight
        AppColorScheme.EMBER -> if (isDark) EmberDark else EmberLight
    }.let { if (isAmoled) it.toAmoled() else it }

    return if (isDark) {
        darkColorScheme(
            primary = colors.primary,
            secondary = colors.secondary,
            tertiary = colors.tertiary,
            surface = colors.surface,
            background = colors.background,
            onPrimary = colors.onPrimary,
            onSecondary = colors.onPrimary,
            onBackground = colors.onBackground,
            onSurface = colors.onSurface
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            secondary = colors.secondary,
            tertiary = colors.tertiary,
            surface = colors.surface,
            background = colors.background,
            onPrimary = colors.onPrimary,
            onSecondary = colors.onPrimary,
            onBackground = colors.onBackground,
            onSurface = colors.onSurface
        )
    }
}