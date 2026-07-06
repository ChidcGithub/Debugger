package com.debugger.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FloatingActions(
    isCapturing: Boolean,
    showExtended: Boolean,
    onToggleCapture: () -> Unit,
    onExport: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val motionScheme = MaterialTheme.motionScheme
    Column(
        modifier = modifier.padding(end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = showExtended,
            enter = fadeIn(
                animationSpec = motionScheme.fastEffectsSpec()
            ) + slideInVertically(
                animationSpec = motionScheme.fastSpatialSpec(),
                initialOffsetY = { it / 2 }
            ),
            exit = fadeOut(
                animationSpec = motionScheme.defaultEffectsSpec()
            ) + slideOutVertically(
                animationSpec = motionScheme.defaultSpatialSpec(),
                targetOffsetY = { it / 2 }
            )
        ) {
            Column(horizontalAlignment = Alignment.End) {
                SmallFloatingActionButton(
                    onClick = onExport,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        Icons.Default.FileDownload,
                        contentDescription = "Export",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                SmallFloatingActionButton(
                    onClick = onClear,
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        FloatingActionButton(
            onClick = onToggleCapture,
            containerColor = if (isCapturing)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = if (isCapturing) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (isCapturing) "Stop" else "Start",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
