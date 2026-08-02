package com.example.data.repository

import com.example.data.database.ScanDao
import com.example.data.database.ScanEntity
import kotlinx.coroutines.flow.Flow

class ScanRepository(private val scanDao: ScanDao) {

    val allScans: Flow<List<ScanEntity>> = scanDao.getAllScans()

    val favoriteScans: Flow<List<ScanEntity>> = scanDao.getFavoriteScans()

    fun searchScans(query: String): Flow<List<ScanEntity>> {
        return if (query.isBlank()) {
            scanDao.getAllScans()
        } else {
            scanDao.searchScans(query)
        }
    }

    suspend fun getScanById(id: Long): ScanEntity? = scanDao.getScanById(id)

    suspend fun getScanByRawValue(rawValue: String): ScanEntity? = scanDao.getScanByRawValue(rawValue)

    suspend fun insertScan(scan: ScanEntity): Long = scanDao.insertScan(scan)

    suspend fun updateScan(scan: ScanEntity) = scanDao.updateScan(scan)

    suspend fun deleteScan(scan: ScanEntity) = scanDao.deleteScan(scan)

    suspend fun deleteScanById(id: Long) = scanDao.deleteScanById(id)

    suspend fun clearAllScans() = scanDao.clearAllScans()
}
