package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ScanLaserCyan
import com.example.ui.theme.ScanLaserOrange

@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true,
    overlayColor: Color = Color.Black.copy(alpha = 0.55f),
    cornerColor: Color = ScanLaserOrange,
    laserColor: Color = ScanLaserCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LaserAnimation")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserPosition"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .background(Color.Transparent)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calculate framing box dimensions (e.g. 70% of min dimension)
            val boxWidth = (canvasWidth * 0.72f).coerceAtMost(320.dp.toPx())
            val boxHeight = (boxWidth * 0.85f)
            val left = (canvasWidth - boxWidth) / 2f
            val top = (canvasHeight - boxHeight) / 2f - 40.dp.toPx()
            val right = left + boxWidth
            val bottom = top + boxHeight

            // 1. Draw darkened overlay
            drawRect(color = overlayColor)

            // 2. Clear out scanning target area with rounded corners
            val cornerRadiusPx = 20.dp.toPx()
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(boxWidth, boxHeight),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                blendMode = BlendMode.Clear
            )

            // 3. Draw Corner Brackets
            val cornerLength = 32.dp.toPx()
            val strokeWidth = 5.dp.toPx()

            val cornerPath = Path().apply {
                // Top Left
                moveTo(left, top + cornerLength)
                lineTo(left, top + cornerRadiusPx)
                quadraticTo(left, top, left + cornerRadiusPx, top)
                lineTo(left + cornerLength, top)

                // Top Right
                moveTo(right - cornerLength, top)
                lineTo(right - cornerRadiusPx, top)
                quadraticTo(right, top, right, top + cornerRadiusPx)
                lineTo(right, top + cornerLength)

                // Bottom Right
                moveTo(right, bottom - cornerLength)
                lineTo(right, bottom - cornerRadiusPx)
                quadraticTo(right, bottom, right - cornerRadiusPx, bottom)
                lineTo(right - cornerLength, bottom)

                // Bottom Left
                moveTo(left + cornerLength, bottom)
                lineTo(left + cornerRadiusPx, bottom)
                quadraticTo(left, bottom, left, bottom - cornerRadiusPx)
                lineTo(left, bottom - cornerLength)
            }

            drawPath(
                path = cornerPath,
                color = cornerColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 4. Draw Animated Laser Line
            if (isScanning) {
                val laserY = top + (boxHeight * laserProgress)
                drawLine(
                    color = laserColor,
                    start = Offset(left + 16.dp.toPx(), laserY),
                    end = Offset(right - 16.dp.toPx(), laserY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
