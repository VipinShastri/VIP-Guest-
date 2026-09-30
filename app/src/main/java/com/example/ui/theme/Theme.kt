package com.example.ui.theme

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
    primary = GoldChampagne,
    onPrimary = ObsidianBlack,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = GoldLight,
    onSecondary = ObsidianBlack,
    secondaryContainer = ObsidianSurfaceElevated,
    onSecondaryContainer = CreamIvory,
    tertiary = WineBurgundyLight,
    onTertiary = Color.White,
    background = ObsidianBlack,
    onBackground = CreamIvory,
    surface = ObsidianSurface,
    onSurface = CreamIvory,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = CreamMuted,
    outline = ObsidianBorder,
    outlineVariant = Color(0xFF4A423A)
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = OnGoldContainer,
    onPrimaryContainer = GoldContainer,
    secondary = WineBurgundy,
    onSecondary = Color.White,
    secondaryContainer = LuxuryLightSurfaceElevated,
    onSecondaryContainer = Color(0xFF2C241D),
    tertiary = EmeraldReserve,
    onTertiary = Color.White,
    background = LuxuryLightBg,
    onBackground = Color(0xFF1E1A16),
    surface = LuxuryLightSurface,
    onSurface = Color(0xFF1E1A16),
    surfaceVariant = LuxuryLightSurfaceElevated,
    onSurfaceVariant = Color(0xFF5A524A),
    outline = LuxuryLightBorder,
    outlineVariant = Color(0xFFCCC2B2)
)

@Composable
fun SavoriaTheme(
    darkTheme: Boolean = true, // Default to stunning dark luxury atmosphere
    dynamicColor: Boolean = false, // Keep tailored luxury gold theme
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
