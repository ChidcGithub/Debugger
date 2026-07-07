package com.debugger.app.ui.molecules

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.debugger.app.ui.theme.LogLevelColors

private val springSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

private val springSpecDp = spring<Dp>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilterBar(
    selectedLevels: Set<String>,
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onLevelToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = listOf("V" to "VERBOSE", "D" to "DEBUG", "I" to "INFO", "W" to "WARN", "E" to "ERROR", "F" to "FATAL")
    var searchText by remember(keyword) { mutableStateOf(keyword) }
    var searchExpanded by remember { mutableStateOf(false) }

    val searchHeight by animateDpAsState(
        targetValue = if (searchExpanded) 56.dp else 48.dp,
        animationSpec = spring<Dp>(dampingRatio = 0.6f, stiffness = 300f),
        label = "search_height"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                searchExpanded = it.isNotEmpty()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(searchHeight)
                .semantics { contentDescription = "Search logs" },
            placeholder = { Text("Search logs...") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.semantics { contentDescription = "Search icon" }
                )
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onKeywordChange(searchText) })
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            levels.forEach { (code, name) ->
                val chipColor = LogLevelColors.forLevel(code)
                val containerColor by animateColorAsState(
                    targetValue = if (code in selectedLevels) chipColor.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 350f),
                    label = "chip_$code"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (code in selectedLevels) chipColor
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 350f),
                    label = "chip_label_$code"
                )

                FilterChip(
                    selected = code in selectedLevels,
                    onClick = { onLevelToggle(code) },
                    label = {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (code in selectedLevels)
                                FontWeight.Bold
                            else
                                FontWeight.Medium
                        )
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = containerColor,
                        selectedLabelColor = labelColor,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = "Filter by $name level, ${if (code in selectedLevels) "selected" else "not selected"}"
                    }
                )
            }
        }
    }
}
