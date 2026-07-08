package com.debugger.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val DebuggerShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

object DebuggerLevelShapes {
    val levelIndicator = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
    val levelBadge = RoundedCornerShape(6.dp)
}

object DebuggerExpressiveShapes {
    val logItemCard = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
    val groupedLogItemCard = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp)
}
