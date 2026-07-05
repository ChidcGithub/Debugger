package com.debugger.app.model

data class LogEntry(
    val id: Long = 0,
    val timestamp: String = "",
    val pid: Int = 0,
    val tid: Int = 0,
    val level: String = "I",
    val tag: String = "",
    val message: String = "",
    val isFavorite: Boolean = false
) {
    val levelName: String
        get() = when (level) {
            "V" -> "VERBOSE"
            "D" -> "DEBUG"
            "I" -> "INFO"
            "W" -> "WARN"
            "E" -> "ERROR"
            "F" -> "FATAL"
            else -> "UNKNOWN"
        }
}
