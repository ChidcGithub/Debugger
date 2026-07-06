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

    val OnVerboseContainer = Color(0xFF616161)
    val OnDebugContainer = Color(0xFF1B5E20)
    val OnInfoContainer = Color(0xFF0D47A1)
    val OnWarnContainer = Color(0xFFE65100)
    val OnErrorContainer = Color(0xFFB71C1C)
    val OnFatalContainer = Color(0xFFFFFFFF)

    val VerboseSurface = Color(0xFFF5F5F5)
    val DebugSurface = Color(0xFFF1F8E9)
    val InfoSurface = Color(0xFFE8EAF6)
    val WarnSurface = Color(0xFFFFF8E1)
    val ErrorSurface = Color(0xFFFCE4EC)
    val FatalSurface = Color(0xFFF3E5F5)

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

    fun onContainerForLevel(level: String): Color = when (level) {
        "V" -> OnVerboseContainer
        "D" -> OnDebugContainer
        "I" -> OnInfoContainer
        "W" -> OnWarnContainer
        "E" -> OnErrorContainer
        "F" -> OnFatalContainer
        else -> OnVerboseContainer
    }

    fun surfaceForLevel(level: String): Color = when (level) {
        "V" -> VerboseSurface
        "D" -> DebugSurface
        "I" -> InfoSurface
        "W" -> WarnSurface
        "E" -> ErrorSurface
        "F" -> FatalSurface
        else -> VerboseSurface
    }
}
