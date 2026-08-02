package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "barcode_history")
data class BarcodeItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val format: String, // e.g. "QR_CODE", "CODE_128", "EAN_13", "UPC_A", "ITF", "PDF_417"
    val type: String, // "SCANNED" or "GENERATED"
    val timestamp: Long = System.currentTimeMillis(),
    val title: String = "",
    val fgColorHex: String = "#000000",
    val bgColorHex: String = "#FFFFFF"
)
