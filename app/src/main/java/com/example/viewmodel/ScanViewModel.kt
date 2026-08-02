package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.ScanEntity
import com.example.data.repository.ScanRepository
import com.example.data.repository.SettingsRepository
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val scanDao = AppDatabase.getDatabase(application).scanDao()
    private val scanRepository = ScanRepository(scanDao)
    private val settingsRepository = SettingsRepository(application)

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterCategory = MutableStateFlow("ALL") // "ALL", "QR", "PRODUCT", "FAVORITES"
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    // Scanned Barcode Result Modal State
    private val _activeScanResult = MutableStateFlow<ScanEntity?>(null)
    val activeScanResult: StateFlow<ScanEntity?> = _activeScanResult.asStateFlow()

    // Flashlight & Camera State
    private val _isFlashEnabled = MutableStateFlow(false)
    val isFlashEnabled: StateFlow<Boolean> = _isFlashEnabled.asStateFlow()

    // Settings StateFlows
    val soundEnabled: StateFlow<Boolean> = settingsRepository.soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val vibrationEnabled: StateFlow<Boolean> = settingsRepository.vibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val autoCopyEnabled: StateFlow<Boolean> = settingsRepository.autoCopyEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val themeMode: StateFlow<String> = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val autoFocusEnabled: StateFlow<Boolean> = settingsRepository.autoFocusEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Scan History Flow
    @OptIn(ExperimentalCoroutinesApi::class)
    val historyList: StateFlow<List<ScanEntity>> = combine(_searchQuery, _filterCategory) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        scanRepository.searchScans(query)
    }.combine(_filterCategory) { list, category ->
        when (category) {
            "QR" -> list.filter { it.format.contains("QR") }
            "PRODUCT" -> list.filter { it.format.contains("EAN") || it.format.contains("UPC") || it.format.contains("CODE") }
            "FAVORITES" -> list.filter { it.isFavorite }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ToneGenerator for scan beep
    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    } catch (e: Exception) {
        null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterCategory(category: String) {
        _filterCategory.value = category
    }

    fun toggleFlashlight() {
        _isFlashEnabled.value = !_isFlashEnabled.value
    }

    fun dismissScanResult() {
        _activeScanResult.value = null
    }

    fun onBarcodeDetected(barcode: Barcode) {
        val rawValue = barcode.rawValue ?: barcode.displayValue ?: return
        val displayValue = barcode.displayValue ?: rawValue
        val formatName = getBarcodeFormatName(barcode.format)
        val valueTypeName = getValueTypeName(barcode.valueType)

        viewModelScope.launch {
            // Check if existing scan matches
            val existing = scanRepository.getScanByRawValue(rawValue)
            val scanEntity = if (existing != null) {
                existing.copy(timestamp = System.currentTimeMillis())
            } else {
                ScanEntity(
                    rawValue = rawValue,
                    displayValue = displayValue,
                    format = formatName,
                    valueType = valueTypeName
                )
            }

            val insertedId = scanRepository.insertScan(scanEntity)
            val savedEntity = scanEntity.copy(id = if (scanEntity.id == 0L) insertedId else scanEntity.id)
            _activeScanResult.value = savedEntity

            // Trigger feedback
            if (soundEnabled.value) {
                playBeepSound()
            }
            if (vibrationEnabled.value) {
                triggerVibration()
            }
            if (autoCopyEnabled.value) {
                copyToClipboard(rawValue)
            }
        }
    }

    private fun playBeepSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } catch (_: Exception) {}
    }

    private fun triggerVibration() {
        try {
            val context = getApplication<Application>()
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    fun copyToClipboard(text: String) {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Barcode", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareBarcode(text: String) {
        val context = getApplication<Application>()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Share Barcode").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun toggleFavorite(scan: ScanEntity) {
        viewModelScope.launch {
            val updated = scan.copy(isFavorite = !scan.isFavorite)
            scanRepository.updateScan(updated)
            if (_activeScanResult.value?.id == scan.id) {
                _activeScanResult.value = updated
            }
        }
    }

    fun updateNotes(scan: ScanEntity, notes: String) {
        viewModelScope.launch {
            val updated = scan.copy(notes = notes)
            scanRepository.updateScan(updated)
            if (_activeScanResult.value?.id == scan.id) {
                _activeScanResult.value = updated
            }
        }
    }

    fun deleteScan(scan: ScanEntity) {
        viewModelScope.launch {
            scanRepository.deleteScan(scan)
            if (_activeScanResult.value?.id == scan.id) {
                _activeScanResult.value = null
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            scanRepository.clearAllScans()
        }
    }

    // Export History to CSV
    fun exportHistoryCsv(onComplete: (Uri?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val scans = historyList.value

                val csvHeader = "ID,Format,Value Type,Raw Value,Display Value,Date,Is Favorite,Notes\n"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                val csvContent = StringBuilder(csvHeader)
                for (scan in scans) {
                    val dateStr = dateFormat.format(Date(scan.timestamp))
                    val cleanRaw = scan.rawValue.replace("\"", "\"\"")
                    val cleanDisplay = scan.displayValue.replace("\"", "\"\"")
                    val cleanNotes = scan.notes.replace("\"", "\"\"")

                    csvContent.append("${scan.id},\"${scan.format}\",\"${scan.valueType}\",\"$cleanRaw\",\"$cleanDisplay\",\"$dateStr\",${scan.isFavorite},\"$cleanNotes\"\n")
                }

                val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
                val csvFile = File(exportDir, "scan_history_${System.currentTimeMillis()}.csv")

                FileWriter(csvFile).use { writer ->
                    writer.write(csvContent.toString())
                }

                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    csvFile
                )

                withContext(Dispatchers.Main) {
                    onComplete(contentUri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onComplete(null)
                }
            }
        }
    }

    // Settings Updaters
    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSoundEnabled(enabled) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setVibrationEnabled(enabled) }
    }

    fun setAutoCopyEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAutoCopyEnabled(enabled) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setAutoFocusEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAutoFocusEnabled(enabled) }
    }

    private fun getBarcodeFormatName(format: Int): String {
        return when (format) {
            Barcode.FORMAT_EAN_13 -> "EAN_13"
            Barcode.FORMAT_EAN_8 -> "EAN_8"
            Barcode.FORMAT_UPC_A -> "UPC_A"
            Barcode.FORMAT_UPC_E -> "UPC_E"
            Barcode.FORMAT_CODE_128 -> "CODE_128"
            Barcode.FORMAT_CODE_39 -> "CODE_39"
            Barcode.FORMAT_CODE_93 -> "CODE_93"
            Barcode.FORMAT_CODABAR -> "CODABAR"
            Barcode.FORMAT_DATA_MATRIX -> "DATA_MATRIX"
            Barcode.FORMAT_ITF -> "ITF"
            Barcode.FORMAT_QR_CODE -> "QR_CODE"
            Barcode.FORMAT_PDF417 -> "PDF417"
            Barcode.FORMAT_AZTEC -> "AZTEC"
            else -> "UNKNOWN"
        }
    }

    private fun getValueTypeName(valueType: Int): String {
        return when (valueType) {
            Barcode.TYPE_URL -> "URL"
            Barcode.TYPE_TEXT -> "TEXT"
            Barcode.TYPE_PRODUCT -> "PRODUCT"
            Barcode.TYPE_WIFI -> "WIFI"
            Barcode.TYPE_CONTACT_INFO -> "CONTACT"
            Barcode.TYPE_EMAIL -> "EMAIL"
            Barcode.TYPE_PHONE -> "PHONE"
            Barcode.TYPE_SMS -> "SMS"
            Barcode.TYPE_GEO -> "LOCATION"
            Barcode.TYPE_CALENDAR_EVENT -> "EVENT"
            Barcode.TYPE_DRIVER_LICENSE -> "LICENSE"
            else -> "TEXT"
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
