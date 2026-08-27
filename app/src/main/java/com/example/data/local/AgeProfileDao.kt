package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.AgeProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface AgeProfileDao {
    @Query("SELECT * FROM age_profiles ORDER BY createdTimestamp DESC")
    fun getAllProfiles(): Flow<List<AgeProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: AgeProfile): Long

    @Update
    suspend fun updateProfile(profile: AgeProfile)

    @Query("DELETE FROM age_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Long)

    @Query("DELETE FROM age_profiles")
    suspend fun clearAllProfiles()
}
