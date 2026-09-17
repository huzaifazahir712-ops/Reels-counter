package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reel_scroll_events")
data class ReelScrollEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceApp: String = "TikTok", // "com.zhiliaoapp.musically" or "Interactive Simulator"
    val dwellTimeSeconds: Int = 28, // estimated or tracked seconds spent on this reel
    val dayKey: String, // e.g. "2026-09-16"
    val weekKey: String, // e.g. "2026-W38"
    val hourOfDay: Int // 0 - 23
)
