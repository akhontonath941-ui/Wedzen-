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

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkGoldAccent,
    onPrimary = SlateBlackAccent,
    secondary = DarkMaroonPrimary,
    onSecondary = Color.White,
    tertiary = DarkGoldAccent,
    background = DarkSlateBackground,
    surface = DarkSlateSurface,
    onBackground = IvoryWhiteBackground,
    onSurface = IvoryWhiteBackground
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RoyalMaroonPrimary,
    onPrimary = Color.White,
    secondary = HeritageGoldAccent,
    onSecondary = SlateBlackAccent,
    tertiary = WarmGoldAccent,
    background = IvoryWhiteBackground,
    surface = WarmIvorySurface,
    onBackground = SlateBlackAccent,
    onSurface = SlateBlackAccent
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Force customized branding over Android 12 dynamic wallpaper coloring
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
