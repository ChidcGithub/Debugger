package com.debugger.app.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debugger.app.ui.theme.DebuggerLevelShapes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LevelBadge(level: String, levelColor: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(DebuggerLevelShapes.levelBadge)
            .background(levelColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = level,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
