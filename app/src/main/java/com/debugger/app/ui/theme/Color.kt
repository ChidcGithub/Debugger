package com.debugger.app.ui.theme

import androidx.compose.ui.graphics.Color

object DebuggerColors {
    val PurpleDark = Color(0xFF1A1326)
    val PurpleSurface = Color(0xFF261E38)
    val PurpleSurfaceHigh = Color(0xFF31284D)
    val PurpleSurfaceLow = Color(0xFF1F1830)
    val PurplePrimary = Color(0xFFBB86FC)
    val PurpleOnBg = Color(0xFFEAE0F5)
    val PurpleOnSurface = Color(0xFFD4C7E6)
    val PurpleOutline = Color(0xFF49425A)
}

object LogLevelColors {
    val Verbose = Color(0xFF9E9E9E)
    val Debug = Color(0xFF4CAF50)
    val Info = Color(0xFF2196F3)
    val Warn = Color(0xFFFF9800)
    val Error = Color(0xFFF44336)
    val Fatal = Color(0xFFEF5350)

    fun forLevel(level: String): Color = when (level) {
        "V" -> Verbose
        "D" -> Debug
        "I" -> Info
        "W" -> Warn
        "E" -> Error
        "F" -> Fatal
        else -> Verbose
    }
}
