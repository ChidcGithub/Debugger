package com.debugger.app.ui.atoms

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.graphics.GenericShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val springSpec = spring<Dp>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

private val diagonalClip = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width * 0.3f, size.height)
    lineTo(0f, size.height)
    close()
}

@Composable
fun LevelIndicator(levelColor: Color, expanded: Boolean = true) {
    val indicatorWidth by animateDpAsState(
        targetValue = if (expanded) 6.dp else 0.dp,
        animationSpec = springSpec,
        label = "indicator_width"
    )

    Box(
        modifier = Modifier
            .width(indicatorWidth)
            .fillMaxHeight()
            .clip(diagonalClip)
            .background(levelColor)
    )
}
