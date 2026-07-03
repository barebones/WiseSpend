package com.sandesh.wisespend.ui.theme

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit

object ThemeState {
    var themeMode = mutableStateOf(AppThemeMode.SYSTEM)
        private set

    var colorScheme = mutableStateOf(AppColorScheme.MONO2)
        private set

    private const val PREFS_NAME = "wise_spend_theme"
    private const val KEY_MODE = "theme_mode"
    private const val KEY_SCHEME = "color_scheme"

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        themeMode.value = try {
            AppThemeMode.valueOf(prefs.getString(KEY_MODE, AppThemeMode.SYSTEM.name)!!)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }
        colorScheme.value = try {
            AppColorScheme.valueOf(prefs.getString(KEY_SCHEME, AppColorScheme.MONO2.name)!!)
        } catch (_: Exception) {
            AppColorScheme.MONO2
        }
    }

    fun setMode(context: Context, mode: AppThemeMode) {
        themeMode.value = mode
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putString(KEY_MODE, mode.name) }
    }

    fun setScheme(context: Context, scheme: AppColorScheme) {
        colorScheme.value = scheme
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putString(KEY_SCHEME, scheme.name) }
    }
}