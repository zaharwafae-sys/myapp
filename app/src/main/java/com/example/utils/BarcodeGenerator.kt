package com.example.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

object BarcodeGenerator {

    enum class Format(val displayName: String, val zxingFormat: BarcodeFormat) {
        QR_CODE("QR Code", BarcodeFormat.QR_CODE),
        CODE_128("Code 128", BarcodeFormat.CODE_128),
        EAN_13("EAN-13", BarcodeFormat.EAN_13),
        UPC_A("UPC-A", BarcodeFormat.UPC_A),
        ITF("ITF (Interleaved 2 of 5)", BarcodeFormat.ITF),
        PDF_417("PDF 417", BarcodeFormat.PDF_417)
    }

    fun generateBarcode(
        content: String,
        format: Format = Format.QR_CODE,
        width: Int = 600,
        height: Int = 600,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        centerLogo: Bitmap? = null
    ): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1

            if (format == Format.QR_CODE) {
                hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            }

            // Adjust height for non-square 1D barcodes
            val actualHeight = if (format == Format.QR_CODE) height else (width * 0.4).toInt()

            val writer = MultiFormatWriter()
            val bitMatrix = writer.encode(content, format.zxingFormat, width, actualHeight, hints)
            val matrixWidth = bitMatrix.width
            val matrixHeight = bitMatrix.height

            val pixels = IntArray(matrixWidth * matrixHeight)
            for (y in 0 until matrixHeight) {
                val offset = y * matrixWidth
                for (x in 0 until matrixWidth) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) foregroundColor else backgroundColor
                }
            }

            val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)

            // If QR code and logo bitmap provided, overlay logo in center
            if (format == Format.QR_CODE && centerLogo != null) {
                overlayLogo(bitmap, centerLogo)
            } else {
                bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun overlayLogo(qrBitmap: Bitmap, logoBitmap: Bitmap): Bitmap {
        val config = qrBitmap.config ?: Bitmap.Config.ARGB_8888
        val combined = Bitmap.createBitmap(qrBitmap.width, qrBitmap.height, config)
        val canvas = Canvas(combined)
        canvas.drawBitmap(qrBitmap, 0f, 0f, null)

        // Calculate size of center logo (max 20% of QR size for scan reliability)
        val logoWidth = (qrBitmap.width * 0.22f).toInt()
        val logoHeight = (qrBitmap.height * 0.22f).toInt()
        val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoWidth, logoHeight, true)

        val left = (qrBitmap.width - logoWidth) / 2f
        val top = (qrBitmap.height - logoHeight) / 2f

        // Draw white background circle behind logo for clarity
        val paint = android.graphics.Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
        }
        val radius = (logoWidth / 2f) + 6f
        canvas.drawCircle(qrBitmap.width / 2f, qrBitmap.height / 2f, radius, paint)

        canvas.drawBitmap(scaledLogo, left, top, null)
        return combined
    }
}
