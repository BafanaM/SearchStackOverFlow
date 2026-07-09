package com.example.stackoverflow.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

// Coordinates lifted from the stack_overflow_icon vector's 120x120 viewport, split into the
// discrete shapes the logo is actually built from (a base tray + 5 flame bars), listed bottom to
// top so they can be revealed one at a time.
private val baseShapePoints = listOf(
    84.4f to 93.8f,
    84.4f to 70.6f,
    92.1f to 70.6f,
    92.1f to 101.5f,
    22.6f to 101.5f,
    22.6f to 70.6f,
    30.3f to 70.6f,
    30.3f to 93.8f,
)
private val barEPoints = listOf(38f to 86f, 76.6f to 86f, 76.6f to 78.3f, 38f to 78.3f)
private val barAPoints = listOf(38.8f to 68.4f, 76.6f to 76.3f, 78.2f to 68.7f, 40.4f to 60.8f)
private val barBPoints = listOf(43.8f to 50.4f, 78.8f to 66.7f, 82.0f to 59.7f, 47.0f to 43.3f)
private val barCPoints = listOf(53.5f to 33.2f, 83.2f to 57.9f, 88.1f to 52.0f, 58.4f to 27.3f)
private val barDPoints = listOf(72.7f to 14.9f, 66.5f to 19.5f, 89.5f to 50.5f, 95.7f to 45.9f)

private val grayBase = Color(0xFFBCBBBB)
private val orangeFlame = Color(0xFFF48023)

private data class LogoShape(val points: List<Pair<Float, Float>>, val color: Color)

private val logoShapesBottomToTop = listOf(
    LogoShape(baseShapePoints, grayBase),
    LogoShape(barEPoints, orangeFlame),
    LogoShape(barAPoints, orangeFlame),
    LogoShape(barBPoints, orangeFlame),
    LogoShape(barCPoints, orangeFlame),
    LogoShape(barDPoints, orangeFlame),
)

@Composable
fun AnimatedStackOverflowLogo(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "loading")
    // Progress sweeps from 0 to shapes.size while drawing, then keeps climbing for a short hold
    // at "fully drawn" before snapping back to 0 and restarting.
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = logoShapesBottomToTop.size + 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "logoDrawProgress",
    )

    Canvas(
        modifier = modifier
            .size(96.dp)
            .semantics { contentDescription = "Loading" },
    ) {
        val scaleX = size.width / 120f
        val scaleY = size.height / 120f

        logoShapesBottomToTop.forEachIndexed { index, shape ->
            val alpha = (progress - index).coerceIn(0f, 1f)
            if (alpha > 0f) {
                val path = Path().apply {
                    shape.points.forEachIndexed { pointIndex, (x, y) ->
                        val px = x * scaleX
                        val py = y * scaleY
                        if (pointIndex == 0) moveTo(px, py) else lineTo(px, py)
                    }
                    close()
                }
                drawPath(path, color = shape.color, alpha = alpha)
            }
        }
    }
}

@Composable
fun FullScreenLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnimatedStackOverflowLogo()
    }
}

@Composable
fun FullScreenError(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
