package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayDateFormat = SimpleDateFormat("EEE, MMM d", Locale.US)
    private val shortDayFormat = SimpleDateFormat("EEE", Locale.US)
    private val shortDateOnlyFormat = SimpleDateFormat("MMM d", Locale.US)

    fun getCurrentDayKey(): String {
        return dayFormat.format(Date())
    }

    fun getDayKey(timestamp: Long): String {
        return dayFormat.format(Date(timestamp))
    }

    fun getHourOfDay(timestamp: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return cal.get(Calendar.HOUR_OF_DAY)
    }

    /**
     * Returns a string representing year-week, e.g. "2026-W38"
     */
    fun getWeekKey(timestamp: Long = System.currentTimeMillis()): String {
        val cal = Calendar.getInstance(Locale.US)
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.minimalDaysInFirstWeek = 4
        cal.timeInMillis = timestamp
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        return String.format(Locale.US, "%d-W%02d", year, week)
    }

    /**
     * Shifts a weekKey by offset (+1 for next week, -1 for previous week)
     */
    fun getShiftedWeekKey(currentWeekKey: String, weekOffset: Int): String {
        val (year, week) = parseWeekKey(currentWeekKey)
        val cal = Calendar.getInstance(Locale.US)
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.clear()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.WEEK_OF_YEAR, week)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.add(Calendar.WEEK_OF_YEAR, weekOffset)
        val newYear = cal.get(Calendar.YEAR)
        val newWeek = cal.get(Calendar.WEEK_OF_YEAR)
        return String.format(Locale.US, "%d-W%02d", newYear, newWeek)
    }

    fun parseWeekKey(weekKey: String): Pair<Int, Int> {
        return try {
            val parts = weekKey.split("-W")
            Pair(parts[0].toInt(), parts[1].toInt())
        } catch (e: Exception) {
            val cal = Calendar.getInstance(Locale.US)
            Pair(cal.get(Calendar.YEAR), cal.get(Calendar.WEEK_OF_YEAR))
        }
    }

    /**
     * Returns the 7 day keys (Monday to Sunday) for the given weekKey
     */
    fun getDaysForWeek(weekKey: String): List<WeekDayInfo> {
        val (year, week) = parseWeekKey(weekKey)
        val cal = Calendar.getInstance(Locale.US)
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.clear()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.WEEK_OF_YEAR, week)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val days = mutableListOf<WeekDayInfo>()
        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        for (i in 0..6) {
            val date = cal.time
            val dayKey = dayFormat.format(date)
            val displayDate = shortDateOnlyFormat.format(date)
            days.add(WeekDayInfo(dayKey = dayKey, dayLabel = dayNames[i], displayDate = displayDate))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    fun getWeekRangeLabel(weekKey: String): String {
        val days = getDaysForWeek(weekKey)
        if (days.isEmpty()) return weekKey
        return "${days.first().displayDate} – ${days.last().displayDate}"
    }

    fun formatDuration(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }

    fun formatTime(timestamp: Long): String {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
        return timeFormat.format(Date(timestamp))
    }
}

data class WeekDayInfo(
    val dayKey: String,
    val dayLabel: String,
    val displayDate: String
)
