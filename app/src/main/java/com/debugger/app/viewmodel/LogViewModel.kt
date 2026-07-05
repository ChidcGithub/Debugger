package com.debugger.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.debugger.app.bridge.RustBridge
import com.debugger.app.bridge.LogCallback
import com.debugger.app.model.LogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

data class LogFilter(
    val levels: Set<String> = emptySet(),
    val keyword: String = "",
    val pid: Int? = null,
    val favoritesOnly: Boolean = false
)

data class LogStats(
    val total: Long = 0,
    val levels: Map<String, Long> = emptyMap(),
    val topTags: List<Pair<String, Long>> = emptyList()
)

class LogViewModel(application: Application) : AndroidViewModel(application), LogCallback {

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    private val _filter = MutableStateFlow(LogFilter())
    val filter: StateFlow<LogFilter> = _filter.asStateFlow()

    private val _isCapturing = MutableStateFlow(false)
    val isCapturing: StateFlow<Boolean> = _isCapturing.asStateFlow()

    private val _stats = MutableStateFlow(LogStats())
    val stats: StateFlow<LogStats> = _stats.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val allEntries = mutableListOf<LogEntry>()

    init {
        RustBridge.initialize(application, this)
        refreshLogs()
        refreshStats()
    }

    override fun onLogEntry(json: String) {
        viewModelScope.launch {
            try {
                val obj = JSONObject(json)
                val entry = LogEntry(
                    id = obj.optLong("id"),
                    timestamp = obj.optString("timestamp"),
                    pid = obj.optInt("pid"),
                    tid = obj.optInt("tid"),
                    level = obj.optString("level"),
                    tag = obj.optString("tag"),
                    message = obj.optString("message"),
                    isFavorite = obj.optBoolean("is_favorite")
                )
                allEntries.add(0, entry)
                applyFilter()
            } catch (_: Exception) { }
        }
    }

    override fun onError(message: String) {
        viewModelScope.launch {
            _error.value = message
        }
    }

    override fun onCaptureStateChanged(isRunning: Boolean) {
        viewModelScope.launch {
            _isCapturing.value = isRunning
        }
    }

    fun toggleCapture() {
        if (_isCapturing.value) {
            RustBridge.stopCapture()
        } else {
            RustBridge.startCapture()
        }
    }

    fun updateFilter(newFilter: LogFilter) {
        _filter.value = newFilter
        refreshLogs()
    }

    fun setKeyword(keyword: String) {
        _filter.value = _filter.value.copy(keyword = keyword)
        applyFilter()
    }

    fun toggleLevel(level: String) {
        val current = _filter.value.levels
        val updated = if (level in current) current - level else current + level
        _filter.value = _filter.value.copy(levels = updated)
        applyFilter()
    }

    fun clearError() {
        _error.value = null
    }

    fun refreshLogs() {
        viewModelScope.launch {
            try {
                val filterJson = buildFilterJson()
                val result = RustBridge.getLogs(filterJson)
                val entries = parseLogEntries(result)
                _logs.value = entries
            } catch (_: Exception) { }
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            try {
                val result = RustBridge.getStats()
                val json = JSONObject(result)
                val total = json.optLong("total")
                val levels = mutableMapOf<String, Long>()
                json.optJSONObject("levels")?.keys()?.forEach { key ->
                    levels[key] = json.optJSONObject("levels")?.optLong(key) ?: 0L
                }
                val topTags = json.optJSONArray("top_tags")?.let { arr ->
                    (0 until arr.length()).map {
                        val obj = arr.getJSONObject(it)
                        obj.optString("tag") to obj.optLong("count")
                    }
                } ?: emptyList()
                _stats.value = LogStats(total = total, levels = levels, topTags = topTags)
            } catch (_: Exception) { }
        }
    }

    fun exportLogs(path: String, format: String) {
        viewModelScope.launch {
            try {
                val filterJson = buildFilterJson()
                RustBridge.exportLogs(path, format, filterJson)
            } catch (_: Exception) { }
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            RustBridge.clearLogs()
            allEntries.clear()
            _logs.value = emptyList()
            refreshStats()
        }
    }

    private fun buildFilterJson(): String {
        val filter = _filter.value
        val obj = JSONObject()
        if (filter.levels.isNotEmpty()) {
            obj.put("levels", JSONArray(filter.levels.toList()))
        }
        if (filter.keyword.isNotBlank()) {
            obj.put("keyword", filter.keyword)
        }
        filter.pid?.let { obj.put("pid", it) }
        if (filter.favoritesOnly) {
            obj.put("favorites_only", true)
        }
        return obj.toString()
    }

    private fun parseLogEntries(json: String): List<LogEntry> {
        val entries = mutableListOf<LogEntry>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                entries.add(
                    LogEntry(
                        id = obj.optLong("id"),
                        timestamp = obj.optString("timestamp"),
                        pid = obj.optInt("pid"),
                        tid = obj.optInt("tid"),
                        level = obj.optString("level"),
                        tag = obj.optString("tag"),
                        message = obj.optString("message"),
                        isFavorite = obj.optBoolean("is_favorite")
                    )
                )
            }
        } catch (_: Exception) { }
        return entries
    }

    private fun applyFilter() {
        val filter = _filter.value
        _logs.value = allEntries.filter { entry ->
            (filter.levels.isEmpty() || entry.level in filter.levels) &&
                (filter.keyword.isBlank() ||
                    entry.tag.contains(filter.keyword, ignoreCase = true) ||
                    entry.message.contains(filter.keyword, ignoreCase = true)) &&
                (filter.pid == null || entry.pid == filter.pid) &&
                (!filter.favoritesOnly || entry.isFavorite)
        }
    }
}
