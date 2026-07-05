package com.debugger.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.debugger.app.ui.screens.ExportScreen
import com.debugger.app.ui.screens.LogDetailScreen
import com.debugger.app.ui.screens.LogListScreen
import com.debugger.app.ui.screens.SettingsScreen
import com.debugger.app.ui.theme.DebuggerTheme
import com.debugger.app.viewmodel.LogViewModel

enum class Screen {
    LogList, LogDetail, Export, Settings
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DebuggerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DebuggerApp()
                }
            }
        }
    }
}

@Composable
fun DebuggerApp(viewModel: LogViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(Screen.LogList) }
    var selectedLogId by remember { mutableStateOf<Long?>(null) }

    when (currentScreen) {
        Screen.LogList -> LogListScreen(
            viewModel = viewModel,
            onNavigateToDetail = { logId ->
                selectedLogId = logId
                currentScreen = Screen.LogDetail
            },
            onNavigateToExport = {
                currentScreen = Screen.Export
            },
            onNavigateToSettings = {
                currentScreen = Screen.Settings
            }
        )
        Screen.LogDetail -> LogDetailScreen(
            logId = selectedLogId ?: 0L,
            viewModel = viewModel,
            onBack = { currentScreen = Screen.LogList }
        )
        Screen.Export -> ExportScreen(
            viewModel = viewModel,
            onBack = { currentScreen = Screen.LogList }
        )
        Screen.Settings -> SettingsScreen(
            stats = viewModel.stats,
            maxEntries = viewModel.maxEntries,
            autoScroll = viewModel.autoScroll,
            onMaxEntriesChange = { viewModel.updateMaxEntries(it) },
            onAutoScrollToggle = { viewModel.toggleAutoScroll() },
            onBack = { currentScreen = Screen.LogList }
        )
    }
}
