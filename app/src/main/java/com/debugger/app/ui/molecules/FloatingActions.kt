package com.debugger.app.ui.molecules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FabMenuItem(
                    label = "Export",
                    icon = Icons.Default.FileDownload,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = onExport
                )
                FabMenuItem(
                    label = "Clear",
                    icon = Icons.Default.Delete,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    onClick = onClear
                )
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

@Composable
private fun FabMenuItem(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    val cornerDp by animateDpAsState(
        targetValue = 16.dp,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 350f),
        label = "fab_menu_corner"
    )
    val menuShape = remember(cornerDp) { RoundedCornerShape(bottomStart = cornerDp, topEnd = cornerDp) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = containerColor.copy(alpha = 0.9f)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = containerColor,
            modifier = Modifier
                .size(48.dp)
                .clip(menuShape)
                .semantics { contentDescription = label }
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = contentColor
            )
        }
    }
}
