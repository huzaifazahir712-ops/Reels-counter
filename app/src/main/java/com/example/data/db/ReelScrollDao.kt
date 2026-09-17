package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ReelScrollEvent
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelScrollDao {

    @Query("SELECT * FROM reel_scroll_events ORDER BY timestamp DESC")
    fun getAllEventsFlow(): Flow<List<ReelScrollEvent>>

    @Query("SELECT * FROM reel_scroll_events WHERE weekKey = :weekKey ORDER BY timestamp ASC")
    fun getEventsForWeekFlow(weekKey: String): Flow<List<ReelScrollEvent>>

    @Query("SELECT * FROM reel_scroll_events WHERE dayKey = :dayKey ORDER BY timestamp DESC")
    fun getEventsForDayFlow(dayKey: String): Flow<List<ReelScrollEvent>>

    @Query("SELECT * FROM reel_scroll_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEventsFlow(limit: Int): Flow<List<ReelScrollEvent>>

    @Query("SELECT COUNT(*) FROM reel_scroll_events")
    fun getTotalReelsCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM reel_scroll_events WHERE dayKey = :dayKey")
    fun getTodayReelsCountFlow(dayKey: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM reel_scroll_events")
    suspend fun getTotalEventsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ReelScrollEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<ReelScrollEvent>)

    @Query("DELETE FROM reel_scroll_events WHERE weekKey = :weekKey")
    suspend fun deleteEventsForWeek(weekKey: String)

    @Query("DELETE FROM reel_scroll_events")
    suspend fun clearAllEvents()

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<UserSettings?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: UserSettings)
}
