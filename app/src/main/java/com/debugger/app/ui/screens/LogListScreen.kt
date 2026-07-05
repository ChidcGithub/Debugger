package com.debugger.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debugger.app.ui.components.FilterBar
import com.debugger.app.ui.components.FloatingActions
import com.debugger.app.ui.components.LogItem
import com.debugger.app.viewmodel.LogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogListScreen(
    viewModel: LogViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToExport: () -> Unit
) {
    val logs by viewModel.logs.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val isCapturing by viewModel.isCapturing.collectAsState()
    val error by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showActions by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Debugger",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActions(
                isCapturing = isCapturing,
                showExtended = showActions,
                onToggleCapture = { viewModel.toggleCapture() },
                onExport = onNavigateToExport,
                onClear = { viewModel.clearAllLogs() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FilterBar(
                selectedLevels = filter.levels,
                keyword = filter.keyword,
                onKeywordChange = { viewModel.setKeyword(it) },
                onLevelToggle = { viewModel.toggleLevel(it) }
            )

            if (logs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No logs yet.\nTap the play button to start capturing.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        top = 4.dp,
                        bottom = 88.dp
                    )
                ) {
                    items(
                        items = logs,
                        key = { it.id }
                    ) { entry ->
                        LogItem(
                            entry = entry,
                            onClick = { onNavigateToDetail(entry.id) },
                            onLongClick = { showActions = !showActions }
                        )
                    }
                }
            }
        }
    }
}
