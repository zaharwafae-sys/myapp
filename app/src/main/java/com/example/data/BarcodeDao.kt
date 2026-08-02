package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM barcode_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<BarcodeItem>>

    @Query("SELECT * FROM barcode_history WHERE type = :type ORDER BY timestamp DESC")
    fun getHistoryByType(type: String): Flow<List<BarcodeItem>>

    @Query("SELECT * FROM barcode_history WHERE content LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<BarcodeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: BarcodeItem): Long

    @Query("DELETE FROM barcode_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM barcode_history")
    suspend fun clearAll()
}
