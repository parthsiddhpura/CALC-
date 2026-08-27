package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "age_profiles")
data class AgeProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val birthYear: Int,
    val birthMonth: Int,
    val birthDay: Int,
    val birthHour: Int = 12,
    val birthMinute: Int = 0,
    val notes: String = "",
    val relationship: String = "Friend", // Self, Family, Friend, Colleague, Other
    val createdTimestamp: Long = System.currentTimeMillis()
)
