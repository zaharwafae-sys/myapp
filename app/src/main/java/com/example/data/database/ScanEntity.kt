package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawValue: String,
    val displayValue: String,
    val format: String, // e.g. "EAN_13", "QR_CODE", "CODE_128", "UPC_A"
    val valueType: String, // e.g. "URL", "TEXT", "WIFI", "PRODUCT", "EMAIL", "PHONE"
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val notes: String = ""
)
