package com.debugger.app.ui.organisms

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val springFloatSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

private val springDpSpec = spring<androidx.compose.ui.unit.Dp>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GradientTopBar(
    title: String,
    collapsedFraction: Float = 0f,
    navigationIcon: ImageVector = Icons.Default.ArrowBack,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val expandedHeight = TopAppBarDefaults.TopAppBarExpandedHeight
    val collapsedHeight = 64.dp
    val expandedTitleAlpha = 1f - collapsedFraction.coerceIn(0f, 1f)
    val collapsedTitleAlpha = collapsedFraction.coerceIn(0.5f, 1f).let { (it - 0.5f) * 2f }

    val topBarHeight by animateDpAsState(
        targetValue = expandedHeight - (expandedHeight - collapsedHeight) * collapsedFraction,
        animationSpec = springDpSpec,
        label = "topbar_height"
    )

    val cornerRadius by animateDpAsState(
        targetValue = (28.dp * (1f - collapsedFraction)).coerceAtLeast(0.dp),
        animationSpec = springDpSpec,
        label = "corner_radius"
    )

    val titleScale by animateFloatAsState(
        targetValue = 1f - collapsedFraction * 0.4f,
        animationSpec = springFloatSpec,
        label = "title_scale"
    )

    val scrimAlpha by animateFloatAsState(
        targetValue = collapsedFraction * 0.85f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "scrim_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(topBarHeight)
                .clip(RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

        if (scrimAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarHeight)
                    .alpha(scrimAlpha)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(collapsedHeight)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onNavigateBack != null) {
                    FilledIconButton(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(50),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Back" }
                    ) {
                        Icon(
                            imageVector = navigationIcon,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .alpha(collapsedTitleAlpha.coerceIn(0f, 1f))
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actions()
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .alpha(expandedTitleAlpha)
                        .graphicsLayer {
                            scaleX = titleScale
                            scaleY = titleScale
                        }
                )
            }
        }
    }
}
