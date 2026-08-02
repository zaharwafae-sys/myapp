package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BarcodeItem
import com.example.data.BarcodeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BarcodeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BarcodeRepository

    val filterType = MutableStateFlow("ALL") // "ALL", "SCANNED", "GENERATED"
    val searchQuery = MutableStateFlow("")

    private val _themeMode = MutableStateFlow("SYSTEM") // "SYSTEM", "DARK", "LIGHT"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).barcodeDao()
        repository = BarcodeRepository(dao)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val historyItems: StateFlow<List<BarcodeItem>> = searchQuery
        .flatMapLatest { query ->
            if (query.isNotBlank()) {
                repository.searchHistory(query)
            } else {
                when (filterType.value) {
                    "SCANNED" -> repository.getHistoryByType("SCANNED")
                    "GENERATED" -> repository.getHistoryByType("GENERATED")
                    else -> repository.allHistory
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFilter(type: String) {
        filterType.value = type
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    fun saveScannedCode(content: String, format: String, title: String = "") {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.insert(
                BarcodeItem(
                    content = content,
                    format = format,
                    type = "SCANNED",
                    title = if (title.isBlank()) "ممسوح: $format" else title
                )
            )
        }
    }

    fun saveGeneratedCode(
        content: String,
        format: String,
        title: String = "",
        fgHex: String = "#000000",
        bgHex: String = "#FFFFFF"
    ) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.insert(
                BarcodeItem(
                    content = content,
                    format = format,
                    type = "GENERATED",
                    title = if (title.isBlank()) "مُنشأ: $format" else title,
                    fgColorHex = fgHex,
                    bgColorHex = bgHex
                )
            )
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
