package com.example.data.model

data class WeeklyProductivityReport(
    val weekKey: String,
    val dateRangeLabel: String,
    val isCurrentWeek: Boolean,
    val totalReels: Int,
    val totalTimeSeconds: Long,
    val formattedTotalTime: String,
    val dailyAverageReels: Double,
    val dailyAverageTimeFormatted: String,
    val previousWeekTotalReels: Int,
    val percentChangeFromPreviousWeek: Double?, // e.g. -18.5%
    val productivityScore: Int, // 0 - 100
    val productivityGrade: String, // "A+", "A", "B", "C", "D", "F"
    val productivityGradeLabel: String, // "Laser Focus", "Mindful Use", etc.
    val dailyBreakdown: List<DailyReelMetric>,
    val timeOfDayDistribution: TimeOfDayStats,
    val actionableInsights: List<ProductivityInsight>
)

data class DailyReelMetric(
    val dayKey: String,
    val dayLabel: String, // "Mon", "Tue", etc.
    val displayDate: String, // "Sep 15"
    val reelCount: Int,
    val timeSeconds: Long,
    val isToday: Boolean,
    val isExceedingGoal: Boolean
)

data class TimeOfDayStats(
    val morningReels: Int, // 6 AM - 12 PM
    val afternoonReels: Int, // 12 PM - 5 PM
    val eveningReels: Int, // 5 PM - 10 PM
    val lateNightReels: Int, // 10 PM - 5 AM
    val peakPeriodLabel: String,
    val lateNightPercentage: Int
)

data class ProductivityInsight(
    val id: String,
    val title: String,
    val description: String,
    val type: InsightType, // POSITIVE, WARNING, TIP, NEUTRAL
    val iconName: String
)

enum class InsightType {
    POSITIVE,
    WARNING,
    TIP,
    NEUTRAL
}
