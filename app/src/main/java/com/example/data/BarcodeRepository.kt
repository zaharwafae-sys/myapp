package com.example.data

import kotlinx.coroutines.flow.Flow

class BarcodeRepository(private val dao: BarcodeDao) {
    val allHistory: Flow<List<BarcodeItem>> = dao.getAllHistory()

    fun getHistoryByType(type: String): Flow<List<BarcodeItem>> = dao.getHistoryByType(type)

    fun searchHistory(query: String): Flow<List<BarcodeItem>> = dao.searchHistory(query)

    suspend fun insert(item: BarcodeItem): Long = dao.insertItem(item)

    suspend fun delete(id: Long) = dao.deleteById(id)

    suspend fun clearAll() = dao.clearAll()
}
