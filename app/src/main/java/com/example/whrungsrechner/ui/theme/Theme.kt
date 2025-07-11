package com.example.whrungsrechner.ui.theme

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
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF1C1B1F), // Dunkler Hintergrund
    surface = Color(0xFF1C1B1F), // Dunkle Oberfläche
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE6E1E5), // Heller Text auf dunklem Hintergrund
    onSurface = Color(0xFFE6E1E5), // Heller Text auf dunkler Oberfläche
    surfaceVariant = Color(0xFF49454F), // Eine Variante für Karten etc.
    onSurfaceVariant = Color(0xFFCAC4D0), // Text auf SurfaceVariant
    error = Color.Red // Fehlerfarbe
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE), // Heller Hintergrund
    surface = Color(0xFFFFFBFE), // Helle Oberfläche
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F), // Dunkler Text auf hellem Hintergrund
    onSurface = Color(0xFF1C1B1F), // Dunkler Text auf heller Oberfläche
    surfaceVariant = Color(0xFFE7E0EC), // Eine Variante für Karten etc.
    onSurfaceVariant = Color(0xFF49454F), // Text auf SurfaceVariant
    error = Color.Red // Fehlerfarbe
)

@Composable
fun WährungsrechnerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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