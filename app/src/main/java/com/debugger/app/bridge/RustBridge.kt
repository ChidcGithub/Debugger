package com.debugger.app.bridge

import android.content.Context
import android.util.Log

interface LogCallback {
    fun onLogEntry(json: String)
    fun onError(message: String)
    fun onCaptureStateChanged(isRunning: Boolean)
}

object RustBridge {
    private var callback: LogCallback? = null

    fun initialize(context: Context, cb: LogCallback) {
        callback = cb
        val dbPath = context.getDatabasePath("debugger.db").absolutePath
        dbPath.parentFile?.mkdirs()
        nativeInit(dbPath)
    }

    fun startCapture() {
        nativeStartCapture()
    }

    fun stopCapture() {
        nativeStopCapture()
    }

    fun getLogs(filterJson: String = "{}"): String {
        return nativeGetLogs(filterJson)
    }

    fun exportLogs(path: String, format: String, filterJson: String = "{}"): Boolean {
        return nativeExportLogs(path, format, filterJson)
    }

    fun clearLogs() {
        nativeClearLogs()
    }

    fun getStats(): String {
        return nativeGetStats()
    }

    @JvmStatic
    fun onLogEntry(json: String) {
        callback?.onLogEntry(json)
    }

    @JvmStatic
    fun onError(message: String) {
        Log.e("RustBridge", message)
        callback?.onError(message)
    }

    @JvmStatic
    fun onCaptureStateChanged(isRunning: Boolean) {
        callback?.onCaptureStateChanged(isRunning)
    }

    private external fun nativeInit(dbPath: String)
    private external fun nativeStartCapture()
    private external fun nativeStopCapture()
    private external fun nativeGetLogs(filterJson: String): String
    private external fun nativeExportLogs(path: String, format: String, filterJson: String): Boolean
    private external fun nativeClearLogs()
    private external fun nativeGetStats(): String

    init {
        System.loadLibrary("debugger_core")
    }
}
