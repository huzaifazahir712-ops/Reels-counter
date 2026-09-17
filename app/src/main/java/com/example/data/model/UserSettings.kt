package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val dailyReelGoal: Int = 60, // Maximum recommended reels per day
    val alertThresholdPerSession: Int = 25, // Alert user after continuous scroll
    val estimatedSecondsPerReel: Int = 30, // Average reel length in seconds
    val hapticFeedbackEnabled: Boolean = true,
    val soundChimeEnabled: Boolean = true
)
