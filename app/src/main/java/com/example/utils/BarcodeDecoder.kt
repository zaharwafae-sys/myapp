package com.example.utils

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import java.util.EnumMap

object BarcodeDecoder {

    fun decodeBitmap(bitmap: Bitmap): Result? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            val hints: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)
            hints[DecodeHintType.TRY_HARDER] = true
            hints[DecodeHintType.CHARACTER_SET] = "UTF-8"

            val reader = MultiFormatReader()
            reader.setHints(hints)
            reader.decodeWithState(binaryBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
