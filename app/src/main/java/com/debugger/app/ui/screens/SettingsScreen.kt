package com.debugger.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debugger.app.ui.organisms.GradientTopBar
import com.debugger.app.ui.theme.EmphasizedType
import com.debugger.app.ui.theme.LogLevelColors
import com.debugger.app.viewmodel.LogStats
import kotlinx.coroutines.flow.StateFlow

private val springSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

private val colorSpringSpec = spring<Color>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    stats: StateFlow<LogStats>,
    maxEntries: StateFlow<Int>,
    autoScroll: StateFlow<Boolean>,
    onMaxEntriesChange: (Int) -> Unit,
    onAutoScrollToggle: () -> Unit,
    onBack: () -> Unit
) {
    val statsValue by stats.collectAsState()
    val maxEntriesValue by maxEntries.collectAsState()
    val autoScrollValue by autoScroll.collectAsState()
    val trackColor by animateColorAsState(
        targetValue = if (autoScrollValue)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = colorSpringSpec,
        label = "switch_track"
    )

    Scaffold(
        topBar = {
            GradientTopBar(
                title = "Settings",
                collapsedFraction = 0f,
                onNavigateBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BoxWithConstraints {
                val isWide = maxWidth >= 600.dp

                if (isWide) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            AppInfoCard()
                            Spacer(modifier = Modifier.height(16.dp))
                            StatsCard(stats = statsValue, total = statsValue.total)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            PreferencesCard(
                                maxEntries = maxEntriesValue,
                                autoScroll = autoScrollValue,
                                onMaxEntriesChange = onMaxEntriesChange,
                                onAutoScrollToggle = onAutoScrollToggle,
                                trackColor = trackColor
                            )
                        }
                    }
                } else {
                    Column {
                        AppInfoCard()
                        Spacer(modifier = Modifier.height(16.dp))
                        StatsCard(stats = statsValue, total = statsValue.total)
                        Spacer(modifier = Modifier.height(16.dp))
                        PreferencesCard(
                            maxEntries = maxEntriesValue,
                            autoScroll = autoScrollValue,
                            onMaxEntriesChange = onMaxEntriesChange,
                            onAutoScrollToggle = onAutoScrollToggle,
                            trackColor = trackColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            FooterText()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AppInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Debugger",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "v0.0.1.4",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "A real-time Android logcat viewer",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun StatsCard(stats: LogStats, total: Long) {
    val levelLabels = listOf(
        "V" to "VERBOSE",
        "D" to "DEBUG",
        "I" to "INFO",
        "W" to "WARN",
        "E" to "ERROR",
        "F" to "FATAL"
    )

    val animatedTotal by animateFloatAsState(
        targetValue = total.toFloat(),
        animationSpec = springSpec,
        label = "total_count"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Statistics",
                style = EmphasizedType.titleMediumBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Logs",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatCount(animatedTotal.toLong()),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            levelLabels.forEach { (level, label) ->
                val count = stats.levels[level] ?: 0L
                LevelBarRow(level = level, label = label, count = count, total = total)
                if (level != "F") {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            if (stats.topTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Top Tags",
                    style = EmphasizedType.titleSmallBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                stats.topTags.take(5).forEachIndexed { index, (tag, count) ->
                    TagRow(index = index + 1, tag = tag, count = count)
                    if (index < minOf(stats.topTags.size, 5) - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelBarRow(level: String, label: String, count: Long, total: Long) {
    val levelColor = LogLevelColors.forLevel(level)
    val fraction = if (total > 0) count.toFloat() / total.toFloat() else 0f
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = springSpec,
        label = "bar_$level"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(10.dp)) {
                drawCircle(color = levelColor)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = level,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatCount(count),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(levelColor.copy(alpha = 0.7f))
            )
        }
    }
}

@Composable
private fun TagRow(index: Int, tag: String, count: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$index",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.width(28.dp)
        )
        Text(
            text = tag,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = formatCount(count),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PreferencesCard(
    maxEntries: Int,
    autoScroll: Boolean,
    onMaxEntriesChange: (Int) -> Unit,
    onAutoScrollToggle: () -> Unit,
    trackColor: Color
) {
    var textFieldValue by remember(maxEntries) { mutableStateOf(maxEntries.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Preferences",
                    style = EmphasizedType.titleMediumBold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { value ->
                    textFieldValue = value
                    value.toIntOrNull()?.let { num ->
                        if (num > 0) onMaxEntriesChange(num)
                    }
                },
                label = { Text("Max entries") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Auto scroll",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = autoScroll,
                    onCheckedChange = { onAutoScrollToggle() },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = trackColor
                    )
                )
            }
        }
    }
}

@Composable
private fun FooterText() {
    Text(
        text = "Built with Kotlin · Rust · Material 3 Expressive",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

private fun formatCount(count: Long): String = when {
    count >= 1_000_000 -> "${count / 1_000_000}.${(count % 1_000_000) / 100_000}M"
    count >= 1_000 -> "${count / 1_000}.${(count % 1_000) / 100}k"
    else -> count.toString()
}
