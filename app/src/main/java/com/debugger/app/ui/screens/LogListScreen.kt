package com.debugger.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import com.debugger.app.ui.molecules.LogItem
import com.debugger.app.ui.organisms.GradientTopBar
import com.debugger.app.viewmodel.LogViewModel

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

    Scaffold(
        topBar = {
            GradientTopBar(
                title = if (logs.isNotEmpty()) "Debugger (${logs.size})" else "Debugger",
                collapsedFraction = collapsedFraction,
                onNavigateBack = null,
                actions = {
                    IconButton(onClick = { viewModel.toggleFoldSimilar() }) {
                        Icon(
                            if (foldSimilar) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                            contentDescription = if (foldSimilar) "Unfold" else "Fold"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
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

            val motionScheme = MaterialTheme.motionScheme
            AnimatedContent(
                targetState = logs.isEmpty(),
                transitionSpec = {
                    fadeIn(
                        animationSpec = motionScheme.defaultEffectsSpec()
                    ) togetherWith fadeOut(
                        animationSpec = motionScheme.fastEffectsSpec()
                    )
                },
                label = "empty_content"
            ) { isEmpty ->
                if (isEmpty) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(
                            top = 4.dp,
                            bottom = 80.dp
                        )
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
                                    onClick = { onNavigateToDetail(item.entry.id) },
                                    onLongClick = { showActions = !showActions }
                                )
                                is LogDisplayItem.FoldedGroup -> GroupedLogItem(
                                    entry = item.representative,
                                    count = item.count,
                                    onClick = { onNavigateToDetail(item.representative.id) },
                                    onLongClick = { showActions = !showActions }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
