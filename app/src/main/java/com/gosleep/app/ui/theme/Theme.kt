package com.gosleep.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GoSleepDarkScheme = darkColorScheme(
    primary = GoSleepVioletStart,
    secondary = GoSleepVioletEnd,
    background = NightBackground,
    surface = CardSurface,
    surfaceVariant = CardSurfaceElevated,
    onPrimary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = AccentAmberWarning,
)

@Composable
fun GoSleepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // GoSleep è progettata per restare sempre in dark mode: riduce l'affaticamento
    // visivo nella fascia serale
    MaterialTheme(
        colorScheme = GoSleepDarkScheme,
        typography = GoSleepTypography,
        content = content
    )
}
