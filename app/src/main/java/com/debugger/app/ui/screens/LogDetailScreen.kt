package com.debugger.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debugger.app.ui.molecules.LogDetailContent
import com.debugger.app.ui.organisms.GradientTopBar
import com.debugger.app.viewmodel.LogViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LogDetailScreen(
    logId: Long,
    viewModel: LogViewModel,
    onBack: () -> Unit
) {
    val logs by viewModel.logs.collectAsState()
    val entry = logs.find { it.id == logId }

    Scaffold(
        topBar = {
            GradientTopBar(
                title = "Log Detail",
                collapsedFraction = 0f,
                onNavigateBack = onBack
            )
        }
    ) { padding ->
        if (entry == null) {
            Text(
                text = "Log entry not found",
                modifier = Modifier
                    .padding(padding)
                    .padding(32.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Scaffold
        }

        LogDetailContent(
            entry = entry,
            modifier = Modifier.padding(padding)
        )
    }
}
