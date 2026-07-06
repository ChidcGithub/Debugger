package com.debugger.app.model

sealed interface LogDisplayItem {
    data class Entry(val entry: LogEntry) : LogDisplayItem
    data class FoldedGroup(
        val representative: LogEntry,
        val count: Int,
    ) : LogDisplayItem
}
