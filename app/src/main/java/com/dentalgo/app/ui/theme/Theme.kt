package com.dentalgo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DentalGoColorScheme = lightColorScheme(
    primary          = DentalGoPrimary,
    onPrimary        = DentalGoSurface,
    primaryContainer = DentalGoPrimaryLight,
    secondary        = DentalGoAccent,
    onSecondary      = DentalGoSurface,
    background       = DentalGoBackground,
    onBackground     = DentalGoOnSurface,
    surface          = DentalGoSurface,
    onSurface        = DentalGoOnSurface,
    error            = DentalGoError,
    onError          = DentalGoSurface,
    outline          = DentalGoBorder
)

@Composable
fun DentalGoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DentalGoColorScheme,
        typography  = Typography,
        content     = content
    )
}
