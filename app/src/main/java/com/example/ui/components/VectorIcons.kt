package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AppIcons {

    // 1. App Logo
    val AppLogo: ImageVector by lazy {
        ImageVector.Builder(
            name = "AppLogo",
            defaultWidth = 48.dp,
            defaultHeight = 48.dp,
            viewportWidth = 48f,
            viewportHeight = 48f
        ).apply {
            // Scanner Frame Corners
            path(
                stroke = SolidColor(Color(0xFF1E52E8)),
                strokeLineWidth = 3.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(6f, 15f)
                lineTo(6f, 9f)
                curveTo(6f, 7.34f, 7.34f, 6f, 9f, 6f)
                lineTo(15f, 6f)

                moveTo(33f, 6f)
                lineTo(39f, 6f)
                curveTo(40.66f, 6f, 42f, 7.34f, 42f, 9f)
                lineTo(42f, 15f)

                moveTo(42f, 33f)
                lineTo(42f, 39f)
                curveTo(42f, 40.66f, 40.66f, 42f, 39f, 42f)
                lineTo(33f, 42f)

                moveTo(15f, 42f)
                lineTo(9f, 42f)
                curveTo(7.34f, 42f, 6f, 40.66f, 6f, 39f)
                lineTo(6f, 33f)
            }
            // Barcode Bars
            path(fill = SolidColor(Color(0xFF1E52E8))) {
                moveTo(12f, 14f)
                lineTo(15f, 14f)
                lineTo(15f, 34f)
                lineTo(12f, 34f)
                close()

                moveTo(18f, 14f)
                lineTo(20f, 14f)
                lineTo(20f, 34f)
                lineTo(18f, 34f)
                close()

                moveTo(23f, 14f)
                lineTo(27f, 14f)
                lineTo(27f, 34f)
                lineTo(23f, 34f)
                close()

                moveTo(30f, 14f)
                lineTo(32f, 14f)
                lineTo(32f, 34f)
                lineTo(30f, 34f)
                close()

                moveTo(35f, 14f)
                lineTo(37f, 14f)
                lineTo(37f, 34f)
                lineTo(35f, 34f)
                close()
            }
            // Scanning Laser
            path(
                stroke = SolidColor(Color(0xFFFF6D00)),
                strokeLineWidth = 3f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(8f, 24f)
                lineTo(40f, 24f)
            }
        }.build()
    }

    // 2. Scan Icon
    val Scan: ImageVector = Icons.Default.QrCodeScanner

    // 3. History Icon
    val History: ImageVector = Icons.Outlined.History

    // 4. Flash Icon
    val Flash: ImageVector = Icons.Default.FlashOn

    // 5. Settings Icon
    val Settings: ImageVector = Icons.Default.Settings

    // 6. Share Icon
    val Share: ImageVector = Icons.Default.Share

    // 7. Copy Icon
    val Copy: ImageVector = Icons.Outlined.ContentCopy

    // 8. Delete Icon
    val Delete: ImageVector = Icons.Outlined.Delete

    // 9. Export Icon
    val Export: ImageVector = Icons.Outlined.IosShare

    // 10. About Icon
    val About: ImageVector = Icons.Default.Info
}
