package com.debugger.app.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val DebuggerShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

object DebuggerLevelShapes {
    val levelIndicator = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
    val levelBadge = CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp)
    val levelChip = RoundedCornerShape(6.dp)
}

object DebuggerCardShapes {
    val elevated = RoundedCornerShape(20.dp)
    val contained = RoundedCornerShape(16.dp)
    val tonal = RoundedCornerShape(16.dp)
}
