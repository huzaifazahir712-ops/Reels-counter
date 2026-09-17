package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.model.ReelScrollEvent
import com.example.data.model.UserSettings
import com.example.data.model.WeeklyProductivityReport
import com.example.data.util.ProductivityCalculator
import com.example.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.Random

class ReelRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao = db.reelScrollDao()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        // Initialize default settings and sample data if first run
        repositoryScope.launch {
            if (dao.getSettings() == null) {
                dao.insertSettings(UserSettings())
            }
            if (dao.getTotalEventsCount() == 0) {
                seedInitialSampleData()
            }
        }
    }

    val totalReelsCountFlow: Flow<Int> = dao.getTotalReelsCountFlow().distinctUntilChanged()

    fun getTodayReelsCountFlow(): Flow<Int> {
        val todayKey = DateUtils.getCurrentDayKey()
        return dao.getTodayReelsCountFlow(todayKey).distinctUntilChanged()
    }

    val recentEventsFlow: Flow<List<ReelScrollEvent>> = dao.getRecentEventsFlow(20)

    val userSettingsFlow: Flow<UserSettings> = dao.getSettingsFlow()
        .map { it ?: UserSettings() }
        .distinctUntilChanged()

    fun getWeeklyReportFlow(weekKey: String): Flow<WeeklyProductivityReport> {
        val prevWeekKey = DateUtils.getShiftedWeekKey(weekKey, -1)
        val currentWeekEventsFlow = dao.getEventsForWeekFlow(weekKey)
        val prevWeekEventsFlow = dao.getEventsForWeekFlow(prevWeekKey)
        val settingsFlow = userSettingsFlow

        return combine(currentWeekEventsFlow, prevWeekEventsFlow, settingsFlow) { currentEvents, prevEvents, settings ->
            ProductivityCalculator.generateReport(
                weekKey = weekKey,
                events = currentEvents,
                previousWeekEvents = prevEvents,
                settings = settings
            )
        }
    }

    suspend fun recordReelScroll(
        sourceApp: String = "TikTok",
        dwellTimeSeconds: Int = 26
    ): Long {
        val now = System.currentTimeMillis()
        val event = ReelScrollEvent(
            timestamp = now,
            sourceApp = sourceApp,
            dwellTimeSeconds = dwellTimeSeconds,
            dayKey = DateUtils.getDayKey(now),
            weekKey = DateUtils.getWeekKey(now),
            hourOfDay = DateUtils.getHourOfDay(now)
        )
        return dao.insertEvent(event)
    }

    suspend fun recordBatchReels(count: Int, sourceApp: String = "Interactive Simulator") {
        val now = System.currentTimeMillis()
        val dayKey = DateUtils.getDayKey(now)
        val weekKey = DateUtils.getWeekKey(now)
        val hour = DateUtils.getHourOfDay(now)
        val random = Random()

        val list = (1..count).map { i ->
            ReelScrollEvent(
                timestamp = now - (i * 30_000L),
                sourceApp = sourceApp,
                dwellTimeSeconds = 15 + random.nextInt(35),
                dayKey = dayKey,
                weekKey = weekKey,
                hourOfDay = hour
            )
        }
        dao.insertEvents(list)
    }

    suspend fun updateSettings(settings: UserSettings) {
        dao.insertSettings(settings)
    }

    suspend fun clearAllData() {
        dao.clearAllEvents()
    }

    suspend fun seedInitialSampleData() {
        val currentWeekKey = DateUtils.getWeekKey()
        val prevWeekKey = DateUtils.getShiftedWeekKey(currentWeekKey, -1)
        val random = Random(42)

        val events = mutableListOf<ReelScrollEvent>()

        // Seed previous week (slightly higher scroll rate, e.g. ~45 reels/day)
        val prevDays = DateUtils.getDaysForWeek(prevWeekKey)
        for (day in prevDays) {
            val dailyCount = 35 + random.nextInt(25) // 35 - 59 reels
            for (i in 0 until dailyCount) {
                // Skew towards evening and late night
                val hour = when (random.nextInt(10)) {
                    in 0..1 -> 8 + random.nextInt(4) // morning
                    in 2..4 -> 12 + random.nextInt(5) // afternoon
                    in 5..7 -> 18 + random.nextInt(4) // evening
                    else -> if (random.nextBoolean()) 22 else 23 // late night
                }
                events.add(
                    ReelScrollEvent(
                        timestamp = System.currentTimeMillis() - (14 * 86400_000L) + (random.nextInt(7) * 86400_000L),
                        sourceApp = "com.zhiliaoapp.musically",
                        dwellTimeSeconds = 20 + random.nextInt(30),
                        dayKey = day.dayKey,
                        weekKey = prevWeekKey,
                        hourOfDay = hour
                    )
                )
            }
        }

        // Seed current week up to today
        val currentDays = DateUtils.getDaysForWeek(currentWeekKey)
        val todayKey = DateUtils.getCurrentDayKey()
        for (day in currentDays) {
            // Only seed past or today
            if (day.dayKey <= todayKey) {
                val dailyCount = if (day.dayKey == todayKey) 18 else 20 + random.nextInt(20)
                for (i in 0 until dailyCount) {
                    val hour = when (random.nextInt(10)) {
                        in 0..2 -> 9 + random.nextInt(3)
                        in 3..5 -> 13 + random.nextInt(4)
                        in 6..8 -> 18 + random.nextInt(4)
                        else -> 22
                    }
                    events.add(
                        ReelScrollEvent(
                            timestamp = System.currentTimeMillis() - (random.nextInt(3) * 86400_000L),
                            sourceApp = "com.zhiliaoapp.musically",
                            dwellTimeSeconds = 18 + random.nextInt(32),
                            dayKey = day.dayKey,
                            weekKey = currentWeekKey,
                            hourOfDay = hour
                        )
                    )
                }
            }
        }

        dao.insertEvents(events)
    }

    companion object {
        @Volatile
        private var INSTANCE: ReelRepository? = null

        fun getInstance(context: Context): ReelRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ReelRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
