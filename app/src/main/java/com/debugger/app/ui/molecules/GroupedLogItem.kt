package com.debugger.app.ui.molecules

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.debugger.app.model.LogEntry
import com.debugger.app.ui.atoms.LevelBadge
import com.debugger.app.ui.atoms.LevelIndicator
import com.debugger.app.ui.atoms.LogTimestamp
import com.debugger.app.ui.theme.EmphasizedType
import com.debugger.app.ui.theme.LogLevelColors

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GroupedLogItem(
    entry: LogEntry,
    count: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    index: Int = 0,
    modifier: Modifier = Modifier
) {
    val levelColor = LogLevelColors.forLevel(entry.level)
    var visible by remember { mutableStateOf(false) }
    val shapeDp by animateDpAsState(
        targetValue = if (visible) 16.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 350f),
        label = "corner_morph"
    )
    val itemScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.9f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "item_scale"
    )
    val itemAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 200f),
        label = "item_alpha"
    )
    val cardShape = remember(shapeDp) { RoundedCornerShape(topEnd = shapeDp, bottomStart = shapeDp) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 40L)
        visible = true
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
                this.alpha = itemAlpha
                transformOrigin = TransformOrigin(
                    if (index % 2 == 0) 0f else 1f, 0.5f
                )
            }
            .clip(cardShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .semantics {
                contentDescription = "Grouped log: ${entry.level} ${entry.tag}, $count similar entries"
            },
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    shape = cardShape
                )
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(start = 4.dp)) {
                LevelIndicator(levelColor)
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f).padding(vertical = 8.dp, horizontal = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LevelBadge(entry.level, levelColor)
                        Text(
                            text = entry.tag,
                            style = EmphasizedType.labelLargeBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = levelColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "\u00D7$count",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = levelColor
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        LogTimestamp(entry.timestamp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.message,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (count > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "and ${count - 1} similar ${if (count == 2) "entry" else "entries"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
