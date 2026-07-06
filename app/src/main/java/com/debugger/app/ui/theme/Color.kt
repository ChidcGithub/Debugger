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

    val VerboseSurfaceDark = Color(0xFF2C2C2C)
    val DebugSurfaceDark = Color(0xFF1B3D1B)
    val InfoSurfaceDark = Color(0xFF1A237E)
    val WarnSurfaceDark = Color(0xFF3E2723)
    val ErrorSurfaceDark = Color(0xFF4A1C1C)
    val FatalSurfaceDark = Color(0xFF2D1B4E)

    val VerboseContainerDark = Color(0xFF2C2C2C)
    val DebugContainerDark = Color(0xFF1B3D1B)
    val InfoContainerDark = Color(0xFF1A237E)
    val WarnContainerDark = Color(0xFF3E2723)
    val ErrorContainerDark = Color(0xFF4A1C1C)
    val FatalContainerDark = Color(0xFF2D1B4E)

    val OnVerboseContainerDark = Color(0xFFBDBDBD)
    val OnDebugContainerDark = Color(0xFF81C784)
    val OnInfoContainerDark = Color(0xFF90CAF9)
    val OnWarnContainerDark = Color(0xFFFFAB91)
    val OnErrorContainerDark = Color(0xFFEF9A9A)
    val OnFatalContainerDark = Color(0xFFCE93D8)

    fun forLevel(level: String): Color = when (level) {
        "V" -> Verbose
        "D" -> Debug
        "I" -> Info
        "W" -> Warn
        "E" -> Error
        "F" -> Fatal
        else -> Verbose
    }

    fun containerForLevel(level: String, isDark: Boolean = false): Color = when (level) {
        "V" -> if (isDark) VerboseContainerDark else VerboseContainer
        "D" -> if (isDark) DebugContainerDark else DebugContainer
        "I" -> if (isDark) InfoContainerDark else InfoContainer
        "W" -> if (isDark) WarnContainerDark else WarnContainer
        "E" -> if (isDark) ErrorContainerDark else ErrorContainer
        "F" -> if (isDark) FatalContainerDark else FatalContainer
        else -> VerboseContainer
    }

    fun onContainerForLevel(level: String, isDark: Boolean = false): Color = when (level) {
        "V" -> if (isDark) OnVerboseContainerDark else OnVerboseContainer
        "D" -> if (isDark) OnDebugContainerDark else OnDebugContainer
        "I" -> if (isDark) OnInfoContainerDark else OnInfoContainer
        "W" -> if (isDark) OnWarnContainerDark else OnWarnContainer
        "E" -> if (isDark) OnErrorContainerDark else OnErrorContainer
        "F" -> if (isDark) OnFatalContainerDark else OnFatalContainer
        else -> OnVerboseContainer
    }

    fun surfaceForLevel(level: String, isDark: Boolean = false): Color = when (level) {
        "V" -> if (isDark) VerboseSurfaceDark else VerboseSurface
        "D" -> if (isDark) DebugSurfaceDark else DebugSurface
        "I" -> if (isDark) InfoSurfaceDark else InfoSurface
        "W" -> if (isDark) WarnSurfaceDark else WarnSurface
        "E" -> if (isDark) ErrorSurfaceDark else ErrorSurface
        "F" -> if (isDark) FatalSurfaceDark else FatalSurface
        else -> VerboseSurface
    }
}
