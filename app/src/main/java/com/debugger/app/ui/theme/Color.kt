package com.debugger.app.ui.theme

import androidx.compose.ui.graphics.Color

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
