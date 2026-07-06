package com.debugger.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DebuggerColors.PurplePrimary,
    onPrimary = Color(0xFF1A1326),
    primaryContainer = Color(0xFF4A2080),
    onPrimaryContainer = Color(0xFFEAE0F5),
    secondary = Color(0xFFCE93D8),
    onSecondary = Color(0xFF1A1326),
    tertiary = Color(0xFFFF8A65),
    onTertiary = Color(0xFF1A1326),
    background = DebuggerColors.PurpleDark,
    onBackground = DebuggerColors.PurpleOnBg,
    surface = DebuggerColors.PurpleSurface,
    onSurface = DebuggerColors.PurpleOnSurface,
    surfaceVariant = Color(0xFF31284D),
    onSurfaceVariant = Color(0xFFC2B5D6),
    surfaceContainerLow = DebuggerColors.PurpleSurfaceLow,
    surfaceContainer = DebuggerColors.PurpleSurface,
    surfaceContainerHigh = DebuggerColors.PurpleSurfaceHigh,
    surfaceContainerHighest = Color(0xFF3F3660),
    surfaceBright = Color(0xFF3F3660),
    surfaceDim = DebuggerColors.PurpleDark,
    outline = DebuggerColors.PurpleOutline,
    outlineVariant = Color(0xFF49425A),
    error = Color(0xFFEF9A9A),
    onError = Color(0xFF1A1326),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    inverseSurface = Color(0xFFEAE0F5),
    inverseOnSurface = Color(0xFF1A1326),
    inversePrimary = Color(0xFF7B1FA2),
    scrim = Color(0xFF000000),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF7B1FA2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E5F5),
    onPrimaryContainer = Color(0xFF2D0B3F),
    secondary = Color(0xFF9C27B0),
    onSecondary = Color.White,
    tertiary = Color(0xFFE65100),
    onTertiary = Color.White,
    background = Color(0xFFF7F2FF),
    onBackground = Color(0xFF1A1326),
    surface = Color.White,
    onSurface = Color(0xFF1A1326),
    surfaceVariant = Color(0xFFF3E5F5),
    onSurfaceVariant = Color(0xFF49425A),
    surfaceContainerLow = Color(0xFFFCF8FF),
    surfaceContainer = Color(0xFFF7F2FF),
    surfaceContainerHigh = Color(0xFFF1EBF8),
    surfaceContainerHighest = Color(0xFFEBE5F2),
    outline = Color(0xFF7C7587),
    outlineVariant = Color(0xFFCEC4DB),
    error = Color(0xFFB71C1C),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    inverseSurface = Color(0xFF1A1326),
    inverseOnSurface = Color(0xFFEAE0F5),
    inversePrimary = Color(0xFFBB86FC),
    scrim = Color(0xFF000000),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DebuggerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context).copy(
                background = DebuggerColors.PurpleDark,
                onBackground = DebuggerColors.PurpleOnBg,
                surface = DebuggerColors.PurpleSurface,
                onSurface = DebuggerColors.PurpleOnSurface,
                surfaceContainerLow = DebuggerColors.PurpleSurfaceLow,
                surfaceContainer = DebuggerColors.PurpleSurface,
                surfaceContainerHigh = DebuggerColors.PurpleSurfaceHigh,
                outline = DebuggerColors.PurpleOutline,
                primary = DebuggerColors.PurplePrimary,
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context).copy(
                background = Color(0xFFF7F2FF),
                primary = Color(0xFF7B1FA2),
            )
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DebuggerTypography,
        shapes = DebuggerShapes,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
