package com.debugger.app.ui.screens

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.debugger.app.ui.organisms.GradientTopBar
import com.debugger.app.viewmodel.LogViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    viewModel: LogViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf("txt") }
    var exportSuccess by remember { mutableStateOf(false) }
    var exportPath by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GradientTopBar(
                title = "Export Logs",
                collapsedFraction = 0f,
                onNavigateBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Choose Export Format",
                style = MaterialTheme.typography.titleLarge
            )

            val formats = listOf(
                Triple("txt", "Plain Text", Icons.Default.Description),
                Triple("json", "JSON", Icons.Default.Code),
                Triple("csv", "CSV", Icons.Default.TableChart)
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                formats.forEachIndexed { index, (value, label, icon) ->
                    SegmentedButton(
                        selected = selectedFormat == value,
                        onClick = { selectedFormat = value },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = formats.size
                        ),
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    ) {
                        Text(label)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            exportLog(context, selectedFormat, viewModel)
                        }
                        if (result != null) {
                            exportPath = result
                            exportSuccess = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Export")
            }

            AnimatedVisibility(
                visible = exportSuccess,
                enter = slideInVertically(
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 200f)
                ),
                exit = fadeOut()
            ) {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Exported successfully",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = exportPath,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun exportLog(context: Context, format: String, viewModel: LogViewModel): String? {
    val fileName = "debugger_logs_${System.currentTimeMillis()}.$format"

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, when (format) {
                "json" -> "application/json"
                "csv" -> "text/csv"
                else -> "text/plain"
            })
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: return null
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            val file = File(context.cacheDir, fileName)
            viewModel.exportLogs(file.absolutePath, format)
            file.inputStream().copyTo(stream)
            file.delete()
        }
        "Downloads/$fileName"
    } else {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, fileName)
        viewModel.exportLogs(file.absolutePath, format)
        file.absolutePath
    }
}
