package com.debugger.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debugger.app.model.LogDisplayItem
import com.debugger.app.ui.molecules.FilterBar
import com.debugger.app.ui.molecules.FloatingActions
import com.debugger.app.ui.molecules.GroupedLogItem
import com.debugger.app.ui.molecules.LogDetailContent
import com.debugger.app.ui.molecules.LogItem
import com.debugger.app.ui.organisms.GradientTopBar
import com.debugger.app.viewmodel.LogViewModel

private val compactThreshold = 600.dp
private val listPaneWidth = 380.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LogListScreen(
    viewModel: LogViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToExport: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val logs by viewModel.logs.collectAsState()
    val displayLogs by viewModel.displayLogs.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val isCapturing by viewModel.isCapturing.collectAsState()
    val autoScroll by viewModel.autoScroll.collectAsState()
    val foldSimilar by viewModel.foldSimilar.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedLogId by viewModel.selectedLogId.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var showActions by remember { mutableStateOf(false) }

    val collapsedFraction by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) 1f
            else (listState.firstVisibleItemScrollOffset.toFloat() / 200f).coerceIn(0f, 1f)
        }
    }

    val isAtTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex <= 1 }
    }

    LaunchedEffect(logs.size) {
        if (autoScroll && isAtTop && logs.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    BoxWithConstraints {
        val isCompact = maxWidth < compactThreshold

        if (isCompact) {
            CompactLogList(
                viewModel = viewModel,
                displayLogs = displayLogs,
                isCapturing = isCapturing,
                showActions = showActions,
                collapsedFraction = collapsedFraction,
                foldSimilar = foldSimilar,
                snackbarHostState = snackbarHostState,
                listState = listState,
                onToggleActions = { showActions = it },
                onToggleCapture = { viewModel.toggleCapture() },
                onExport = onNavigateToExport,
                onClear = { viewModel.clearAllLogs() },
                onToggleFold = { viewModel.toggleFoldSimilar() },
                onSettings = onNavigateToSettings,
                onLogClick = { onNavigateToDetail(it) },
                onLogLongClick = { showActions = !showActions },
                onKeywordChange = { viewModel.setKeyword(it) },
                onLevelToggle = { viewModel.toggleLevel(it) },
                filterLevels = filter.levels,
                filterKeyword = filter.keyword
            )
        } else {
            WideLogList(
                viewModel = viewModel,
                displayLogs = displayLogs,
                isCapturing = isCapturing,
                showActions = showActions,
                collapsedFraction = collapsedFraction,
                foldSimilar = foldSimilar,
                selectedLogId = selectedLogId,
                snackbarHostState = snackbarHostState,
                listState = listState,
                logs = logs,
                onToggleActions = { showActions = it },
                onToggleCapture = { viewModel.toggleCapture() },
                onExport = onNavigateToExport,
                onClear = { viewModel.clearAllLogs() },
                onToggleFold = { viewModel.toggleFoldSimilar() },
                onSettings = onNavigateToSettings,
                onLogClick = { viewModel.selectLog(it) },
                onLogLongClick = { showActions = !showActions },
                onKeywordChange = { viewModel.setKeyword(it) },
                onLevelToggle = { viewModel.toggleLevel(it) },
                filterLevels = filter.levels,
                filterKeyword = filter.keyword
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CompactLogList(
    viewModel: LogViewModel,
    displayLogs: List<LogDisplayItem>,
    isCapturing: Boolean,
    showActions: Boolean,
    collapsedFraction: Float,
    foldSimilar: Boolean,
    snackbarHostState: SnackbarHostState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onToggleActions: (Boolean) -> Unit,
    onToggleCapture: () -> Unit,
    onExport: () -> Unit,
    onClear: () -> Unit,
    onToggleFold: () -> Unit,
    onSettings: () -> Unit,
    onLogClick: (Long) -> Unit,
    onLogLongClick: () -> Unit,
    onKeywordChange: (String) -> Unit,
    onLevelToggle: (String) -> Unit,
    filterLevels: Set<String>,
    filterKeyword: String
) {
    val logs by viewModel.logs.collectAsState()

    Scaffold(
        topBar = {
            GradientTopBar(
                title = if (logs.isNotEmpty()) "Debugger (${logs.size})" else "Debugger",
                collapsedFraction = collapsedFraction,
                onNavigateBack = null,
                actions = {
                    IconButton(onClick = onToggleFold) {
                        Icon(
                            if (foldSimilar) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                            contentDescription = if (foldSimilar) "Unfold" else "Fold"
                        )
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActions(
                isCapturing = isCapturing,
                showExtended = showActions,
                onToggleCapture = onToggleCapture,
                onExport = onExport,
                onClear = onClear
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            FilterBar(
                selectedLevels = filterLevels,
                keyword = filterKeyword,
                onKeywordChange = onKeywordChange,
                onLevelToggle = onLevelToggle
            )

            val motionScheme = MaterialTheme.motionScheme
            AnimatedContent(
                targetState = logs.isEmpty(),
                transitionSpec = {
                    fadeIn(animationSpec = motionScheme.defaultEffectsSpec())
                        .togetherWith(fadeOut(animationSpec = motionScheme.fastEffectsSpec()))
                },
                label = "empty_content"
            ) { isEmpty ->
                if (isEmpty) {
                    EmptyLogState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                    ) {
                        items(
                            items = displayLogs,
                            key = { item ->
                                when (item) {
                                    is LogDisplayItem.Entry -> "e_${item.entry.id}"
                                    is LogDisplayItem.FoldedGroup -> "g_${item.representative.id}_${item.count}"
                                }
                            }
                        ) { item ->
                            when (item) {
                                is LogDisplayItem.Entry -> LogItem(
                                    entry = item.entry,
                                    onClick = { onLogClick(item.entry.id) },
                                    onLongClick = onLogLongClick
                                )
                                is LogDisplayItem.FoldedGroup -> GroupedLogItem(
                                    entry = item.representative,
                                    count = item.count,
                                    onClick = { onLogClick(item.representative.id) },
                                    onLongClick = onLogLongClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WideLogList(
    viewModel: LogViewModel,
    displayLogs: List<LogDisplayItem>,
    isCapturing: Boolean,
    showActions: Boolean,
    collapsedFraction: Float,
    foldSimilar: Boolean,
    selectedLogId: Long?,
    snackbarHostState: SnackbarHostState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    logs: List<com.debugger.app.model.LogEntry>,
    onToggleActions: (Boolean) -> Unit,
    onToggleCapture: () -> Unit,
    onExport: () -> Unit,
    onClear: () -> Unit,
    onToggleFold: () -> Unit,
    onSettings: () -> Unit,
    onLogClick: (Long) -> Unit,
    onLogLongClick: () -> Unit,
    onKeywordChange: (String) -> Unit,
    onLevelToggle: (String) -> Unit,
    filterLevels: Set<String>,
    filterKeyword: String
) {
    val selectedEntry = logs.find { it.id == selectedLogId }

    Scaffold(
        topBar = {
            GradientTopBar(
                title = if (logs.isNotEmpty()) "Debugger (${logs.size})" else "Debugger",
                collapsedFraction = collapsedFraction,
                onNavigateBack = null,
                actions = {
                    IconButton(onClick = onToggleFold) {
                        Icon(
                            if (foldSimilar) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                            contentDescription = if (foldSimilar) "Unfold" else "Fold"
                        )
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActions(
                isCapturing = isCapturing,
                showExtended = showActions,
                onToggleCapture = onToggleCapture,
                onExport = onExport,
                onClear = onClear
            )
        }
    ) { padding ->
        Row(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Column(
                modifier = Modifier.width(listPaneWidth).fillMaxHeight()
            ) {
                FilterBar(
                    selectedLevels = filterLevels,
                    keyword = filterKeyword,
                    onKeywordChange = onKeywordChange,
                    onLevelToggle = onLevelToggle
                )

                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyLogState()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
                    ) {
                        items(
                            items = displayLogs,
                            key = { item ->
                                when (item) {
                                    is LogDisplayItem.Entry -> "e_${item.entry.id}"
                                    is LogDisplayItem.FoldedGroup -> "g_${item.representative.id}_${item.count}"
                                }
                            }
                        ) { item ->
                            when (item) {
                                is LogDisplayItem.Entry -> LogItem(
                                    entry = item.entry,
                                    onClick = { onLogClick(item.entry.id) },
                                    onLongClick = onLogLongClick
                                )
                                is LogDisplayItem.FoldedGroup -> GroupedLogItem(
                                    entry = item.representative,
                                    count = item.count,
                                    onClick = { onLogClick(item.representative.id) },
                                    onLongClick = onLogLongClick
                                )
                            }
                        }
                    }
                }
            }

            VerticalDivider()

            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                if (selectedEntry != null) {
                    LogDetailContent(
                        entry = selectedEntry,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "Select a log entry",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLogState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.BugReport,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Text(
                text = "No logs yet.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Tap the play button to start capturing.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
