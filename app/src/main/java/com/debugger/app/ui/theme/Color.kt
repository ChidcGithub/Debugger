package com.debugger.app.ui.theme

import androidx.compose.ui.graphics.Color

object LogLevelColors {
    val Verbose = Color(0xFF9E9E9E)
    val Debug = Color(0xFF4CAF50)
    val Info = Color(0xFF2196F3)
    val Warn = Color(0xFFFF9800)
    val Error = Color(0xFFF44336)
    val Fatal = Color(0xFFB71C1C)

    val VerboseContainer = Color(0xFFF5F5F5)
    val DebugContainer = Color(0xFFE8F5E9)
    val InfoContainer = Color(0xFFE3F2FD)
    val WarnContainer = Color(0xFFFFF3E0)
    val ErrorContainer = Color(0xFFFFEBEE)
    val FatalContainer = Color(0xFFFCE4EC)

    fun forLevel(level: String): Color = when (level) {
        "V" -> Verbose
        "D" -> Debug
        "I" -> Info
        "W" -> Warn
        "E" -> Error
        "F" -> Fatal
        else -> Verbose
    }

    fun containerForLevel(level: String): Color = when (level) {
        "V" -> VerboseContainer
        "D" -> DebugContainer
        "I" -> InfoContainer
        "W" -> WarnContainer
        "E" -> ErrorContainer
        "F" -> FatalContainer
        else -> VerboseContainer
    }

    fun surfaceForLevel(level: String): Color = when (level) {
        "V" -> Color(0xFFF5F5F5)
        "D" -> Color(0xFFF1F8E9)
        "I" -> Color(0xFFE8EAF6)
        "W" -> Color(0xFFFFF8E1)
        "E" -> Color(0xFFFCE4EC)
        "F" -> Color(0xFFF3E5F5)
        else -> Color(0xFFF5F5F5)
    }
}
