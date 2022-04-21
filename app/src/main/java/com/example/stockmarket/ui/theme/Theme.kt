package com.example.stockmarket.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Orange,
    primaryVariant = Orange,
    secondary = DirtyWhite,
    background = Grey
)

private val LightColorPalette = lightColors(
    primary = Orange,
    primaryVariant = Orange,
    secondary = Grey,
    background = DirtyWhite
)

@Composable
fun StockMarketTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
