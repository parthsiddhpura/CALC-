package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String = "STANDARD",
    val isFavorite: Boolean = false,
    val note: String = ""
)
