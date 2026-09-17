package com.example.data.util

import com.example.data.model.DailyReelMetric
import com.example.data.model.InsightType
import com.example.data.model.ProductivityInsight
import com.example.data.model.ReelScrollEvent
import com.example.data.model.TimeOfDayStats
import com.example.data.model.UserSettings
import com.example.data.model.WeeklyProductivityReport
import com.example.util.DateUtils
import kotlin.math.roundToInt

object ProductivityCalculator {

    fun generateReport(
        weekKey: String,
        events: List<ReelScrollEvent>,
        previousWeekEvents: List<ReelScrollEvent>,
        settings: UserSettings
    ): WeeklyProductivityReport {
        val totalReels = events.size
        val totalTimeSeconds = events.sumOf { it.dwellTimeSeconds.toLong() }.coerceAtLeast(
            (totalReels * settings.estimatedSecondsPerReel).toLong()
        )
        val formattedTotalTime = DateUtils.formatDuration(totalTimeSeconds)

        val todayKey = DateUtils.getCurrentDayKey()
        val currentWeekKey = DateUtils.getWeekKey()
        val isCurrentWeek = (weekKey == currentWeekKey)

        // Days calculation
        val weekDays = DateUtils.getDaysForWeek(weekKey)
        val eventsByDay = events.groupBy { it.dayKey }

        val dailyBreakdown = weekDays.map { dayInfo ->
            val dayEvents = eventsByDay[dayInfo.dayKey] ?: emptyList()
            val dayCount = dayEvents.size
            val dayTimeSec = dayEvents.sumOf { it.dwellTimeSeconds.toLong() }
                .coerceAtLeast((dayCount * settings.estimatedSecondsPerReel).toLong())
            DailyReelMetric(
                dayKey = dayInfo.dayKey,
                dayLabel = dayInfo.dayLabel,
                displayDate = dayInfo.displayDate,
                reelCount = dayCount,
                timeSeconds = dayTimeSec,
                isToday = (dayInfo.dayKey == todayKey),
                isExceedingGoal = (dayCount > settings.dailyReelGoal)
            )
        }

        // Active days count for average calculation
        val daysElapsed = if (isCurrentWeek) {
            val todayIndex = weekDays.indexOfFirst { it.dayKey == todayKey }
            if (todayIndex >= 0) todayIndex + 1 else 7
        } else {
            7
        }
        val dailyAverageReels = if (daysElapsed > 0) (totalReels.toDouble() / daysElapsed) else 0.0
        val dailyAverageTimeSeconds = if (daysElapsed > 0) (totalTimeSeconds / daysElapsed) else 0L
        val dailyAverageTimeFormatted = DateUtils.formatDuration(dailyAverageTimeSeconds)

        // Previous week comparison
        val prevTotal = previousWeekEvents.size
        val percentChange: Double? = if (prevTotal > 0) {
            ((totalReels - prevTotal).toDouble() / prevTotal) * 100.0
        } else null

        // Time of Day distribution
        var morning = 0
        var afternoon = 0
        var evening = 0
        var lateNight = 0

        for (event in events) {
            when (event.hourOfDay) {
                in 6..11 -> morning++
                in 12..16 -> afternoon++
                in 17..21 -> evening++
                else -> lateNight++ // 22, 23, 0, 1, 2, 3, 4, 5
            }
        }

        val peakPeriodLabel = when {
            totalReels == 0 -> "None"
            lateNight >= morning && lateNight >= afternoon && lateNight >= evening -> "Late Night (10 PM – 5 AM)"
            evening >= morning && evening >= afternoon && evening >= lateNight -> "Evening (5 PM – 10 PM)"
            afternoon >= morning && afternoon >= evening && afternoon >= lateNight -> "Afternoon (12 PM – 5 PM)"
            else -> "Morning (6 AM – 12 PM)"
        }

        val lateNightPercentage = if (totalReels > 0) {
            ((lateNight.toDouble() / totalReels) * 100).roundToInt()
        } else 0

        // Productivity Score (0 - 100)
        // Normalizing: 0 reels = 100 score. 30 reels/day (210/week) = 75 score. 100 reels/day = 25 score.
        // Penalize late-night doomscrolling proportionally.
        val baseScore = (100.0 - (dailyAverageReels * 0.9)).coerceIn(10.0, 100.0)
        val lateNightPenalty = (lateNightPercentage * 0.3).coerceAtMost(25.0)
        val finalScore = (baseScore - lateNightPenalty).roundToInt().coerceIn(5, 100)

        val (grade, gradeLabel) = when {
            finalScore >= 90 -> Pair("A+", "Super Mindful 🌟")
            finalScore >= 80 -> Pair("A", "High Focus 🎯")
            finalScore >= 70 -> Pair("B", "Healthy Habit 🌿")
            finalScore >= 55 -> Pair("C", "Moderate Scroll ⚡")
            finalScore >= 40 -> Pair("D", "Elevated Screen Time ⚠️")
            else -> Pair("F", "Doomscroll Alert 🚨")
        }

        // Actionable Insights
        val insights = mutableListOf<ProductivityInsight>()

        // 1. Comparison insight
        if (percentChange != null) {
            if (percentChange <= -10.0) {
                insights.add(
                    ProductivityInsight(
                        id = "week_comp_good",
                        title = "Decreased Scrolling! 🎉",
                        description = "You scrolled ${String.format("%.1f", -percentChange)}% fewer reels than last week! That reclaimed roughly ${DateUtils.formatDuration(((prevTotal - totalReels) * settings.estimatedSecondsPerReel).toLong())} of your time.",
                        type = InsightType.POSITIVE,
                        iconName = "trending_down"
                    )
                )
            } else if (percentChange >= 20.0) {
                insights.add(
                    ProductivityInsight(
                        id = "week_comp_warn",
                        title = "Spike in Reel Scrolling 📈",
                        description = "Reels scrolled increased by ${String.format("%.1f", percentChange)}% compared to last week (${totalReels} vs ${prevTotal}). Try setting a 20-minute daily timer.",
                        type = InsightType.WARNING,
                        iconName = "trending_up"
                    )
                )
            }
        }

        // 2. Late night doomscroll insight
        if (lateNightPercentage >= 25 && lateNight > 15) {
            insights.add(
                ProductivityInsight(
                    id = "late_night_warn",
                    title = "Late Night Doomscroll Alert 🌙",
                    description = "$lateNightPercentage% of your reels ($lateNight reels) were scrolled between 10 PM and 5 AM. Bedtime screen exposure reduces REM sleep quality and morning alertness.",
                    type = InsightType.WARNING,
                    iconName = "bedtime"
                )
            )
        } else if (lateNightPercentage < 10 && totalReels > 0) {
            insights.add(
                ProductivityInsight(
                    id = "good_bedtime",
                    title = "Clean Bedtime Routine 🛌",
                    description = "Under 10% of your scrolling occurred late at night. Excellent sleep boundary protection!",
                    type = InsightType.POSITIVE,
                    iconName = "nights_stay"
                )
            )
        }

        // 3. Peak Day insight
        val maxDay = dailyBreakdown.maxByOrNull { it.reelCount }
        if (maxDay != null && maxDay.reelCount > 0) {
            insights.add(
                ProductivityInsight(
                    id = "peak_day",
                    title = "Busiest Day: ${maxDay.dayLabel} (${maxDay.displayDate})",
                    description = "You scrolled ${maxDay.reelCount} reels on ${maxDay.dayLabel}, taking approximately ${DateUtils.formatDuration(maxDay.timeSeconds)}. Consider checking what triggered extra screen time.",
                    type = InsightType.NEUTRAL,
                    iconName = "calendar_today"
                )
            )
        }

        // 4. Time saved / productivity tip
        val estimatedHoursLost = totalTimeSeconds / 3600.0
        if (estimatedHoursLost > 2.0) {
            insights.add(
                ProductivityInsight(
                    id = "productivity_tip",
                    title = "Productivity Swap Tip 💡",
                    description = "Your ${formattedTotalTime} on reels this week equals roughly ${String.format("%.1f", estimatedHoursLost * 20)} pages read of a book or 2 full gym sessions. A 15-minute scroll cap could save 3+ hours every week.",
                    type = InsightType.TIP,
                    iconName = "lightbulb"
                )
            )
        } else {
            insights.add(
                ProductivityInsight(
                    id = "good_balance",
                    title = "Balanced Digital Wellness 🧘",
                    description = "Your reel count is within a very mindful range. You spend less than 30 minutes a day scrolling, leaving ample room for focused deep work.",
                    type = InsightType.POSITIVE,
                    iconName = "verified"
                )
            )
        }

        return WeeklyProductivityReport(
            weekKey = weekKey,
            dateRangeLabel = DateUtils.getWeekRangeLabel(weekKey),
            isCurrentWeek = isCurrentWeek,
            totalReels = totalReels,
            totalTimeSeconds = totalTimeSeconds,
            formattedTotalTime = formattedTotalTime,
            dailyAverageReels = dailyAverageReels,
            dailyAverageTimeFormatted = dailyAverageTimeFormatted,
            previousWeekTotalReels = prevTotal,
            percentChangeFromPreviousWeek = percentChange,
            productivityScore = finalScore,
            productivityGrade = grade,
            productivityGradeLabel = gradeLabel,
            dailyBreakdown = dailyBreakdown,
            timeOfDayDistribution = TimeOfDayStats(
                morningReels = morning,
                afternoonReels = afternoon,
                eveningReels = evening,
                lateNightReels = lateNight,
                peakPeriodLabel = peakPeriodLabel,
                lateNightPercentage = lateNightPercentage
            ),
            actionableInsights = insights
        )
    }
}
