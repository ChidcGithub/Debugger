package com.debugger.app.ui.molecules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private val springSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

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
    val iconRotation by animateFloatAsState(
        targetValue = if (isCapturing) 90f else 0f,
        animationSpec = springSpec,
        label = "fab_rotation"
    )

    Column(
        modifier = modifier.padding(end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = showExtended,
            enter = fadeIn(
                animationSpec = motionScheme.fastEffectsSpec()
            ) + slideInVertically(
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
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
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.semantics { contentDescription = "Export logs" }
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
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.semantics { contentDescription = "Clear all logs" }
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
                MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(64.dp)
                .rotate(iconRotation)
                .semantics {
                    contentDescription = if (isCapturing) "Stop capturing" else "Start capturing"
                }
        ) {
            Icon(
                imageVector = if (isCapturing) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (isCapturing) "Stop" else "Start",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
