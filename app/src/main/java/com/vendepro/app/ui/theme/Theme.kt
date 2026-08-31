package com.vendepro.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// VendePro es una identidad de marca deliberadamente oscura y de un solo
// modo — como Spotify o Discord, no cambia con el tema del sistema.
private val VendeProColorScheme = darkColorScheme(
    primary = Lime,
    onPrimary = OnLime,
    primaryContainer = Lime,
    onPrimaryContainer = OnLime,
    secondary = Pink,
    onSecondary = OnPink,
    secondaryContainer = Pink,
    onSecondaryContainer = OnPink,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnBackground,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = OnBackgroundMuted,
    outline = Outline,
    outlineVariant = Outline,
    error = Danger,
    onError = OnBackground,
)

@Composable
fun VendeProTheme(content: @Composable () -> Unit) {
    // El fondo lo maneja enableEdgeToEdge() en MainActivity (barras
    // transparentes); acá solo pedimos íconos claros, porque el fondo
    // detrás siempre es oscuro en esta identidad de marca.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = VendeProColorScheme,
        typography = VendeProTypography,
        shapes = VendeProShapes,
        content = content,
    )
}
