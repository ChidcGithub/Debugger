package com.debugger.app.ui.atoms

import androidx.compose.animation.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.debugger.app.ui.theme.DebuggerLevelShapes

@Composable
fun LevelIndicator(levelColor: Color, expanded: Boolean = true) {
    val indicatorWidth by animateDpAsState(
        targetValue = if (expanded) 6.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 350f),
        label = "indicator_width"
    )

    Box(
        modifier = Modifier
            .width(indicatorWidth)
            .fillMaxHeight()
            .clip(DebuggerLevelShapes.levelIndicator)
            .background(levelColor)
    )
}
