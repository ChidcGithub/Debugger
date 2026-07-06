package com.debugger.app.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.debugger.app.ui.theme.DebuggerLevelShapes

@Composable
fun LevelIndicator(levelColor: Color) {
    Box(
        modifier = Modifier
            .width(5.dp)
            .fillMaxHeight()
            .clip(DebuggerLevelShapes.levelIndicator)
            .background(levelColor)
    )
}
