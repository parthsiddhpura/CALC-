package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CalculationHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationHistory>>

    @Query("SELECT * FROM calculation_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteHistory(): Flow<List<CalculationHistory>>

    @Query("SELECT * FROM calculation_history WHERE expression LIKE '%' || :query || '%' OR result LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<CalculationHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: CalculationHistory): Long

    @Update
    suspend fun updateHistory(history: CalculationHistory)

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAllHistory()
}
