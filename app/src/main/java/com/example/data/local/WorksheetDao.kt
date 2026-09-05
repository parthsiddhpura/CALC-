package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorksheetDao {
    @Query("SELECT * FROM worksheet_documents ORDER BY updatedAt DESC")
    fun getAllWorksheets(): Flow<List<WorksheetEntity>>

    @Query("SELECT * FROM worksheet_documents WHERE id = :id LIMIT 1")
    suspend fun getWorksheetById(id: String): WorksheetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(worksheet: WorksheetEntity)

    @Query("DELETE FROM worksheet_documents WHERE id = :id")
    suspend fun deleteWorksheet(id: String)
}
