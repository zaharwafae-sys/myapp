package com.example.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareAndCopyUtils {

    fun copyToClipboard(context: Context, text: String, label: String = "Barcode Content") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "تم نسخ النص إلى الحافظة", Toast.LENGTH_SHORT).show()
    }

    fun openUrlInBrowser(context: Context, url: String) {
        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else url

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح الرابط", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareText(context: Context, text: String, title: String = "مشاركة الكود") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun saveBitmapToDevice(context: Context, bitmap: Bitmap, fileName: String = "adam_barcode_${System.currentTimeMillis()}.png"): Uri? {
        return try {
            val cacheDir = context.cacheDir
            val imagesDir = File(cacheDir, "shared_images")
            imagesDir.mkdirs()
            val imageFile = File(imagesDir, fileName)
            val stream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            val authority = "${context.packageName}.provider"
            FileProvider.getUriForFile(context, authority, imageFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareBitmap(context: Context, bitmap: Bitmap, title: String = "مشاركة صورة الكود") {
        val uri = saveBitmapToDevice(context, bitmap)
        if (uri != null) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } else {
            Toast.makeText(context, "تعذر مشاركة الصورة", Toast.LENGTH_SHORT).show()
        }
    }
}
