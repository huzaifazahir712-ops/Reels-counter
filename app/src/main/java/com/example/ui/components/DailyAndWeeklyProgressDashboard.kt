package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DailyReelMetric
import com.example.data.model.UserSettings
import com.example.data.model.WeeklyProductivityReport
import com.example.ui.theme.ColorAlert
import com.example.ui.theme.ColorProductive
import com.example.ui.theme.ColorTikTokCyan
import com.example.ui.theme.ColorTikTokPink
import com.example.ui.theme.ColorWarning
import com.example.util.DateUtils

/**
 * A comprehensive Dashboard Component that displays:
 * 1. Total scroll count for the current day with time spent and goal progress.
 * 2. Summary of weekly progress including weekly total, daily average, trend compared
 *    to last week, productivity score, and a 7-day mini progress strip.
 */
@Composable
fun DailyAndWeeklyProgressDashboard(
    todayScrollCount: Int,
    weeklyReport: WeeklyProductivityReport?,
    userSettings: UserSettings,
    modifier: Modifier = Modifier,
    onViewWeeklyReportClick: (() -> Unit)? = null
) {
    val dailyGoal = userSettings.dailyReelGoal
    val secondsPerReel = userSettings.estimatedSecondsPerReel
    val todayTimeSeconds = (todayScrollCount * secondsPerReel).toLong()
    val isTodayExceeded = todayScrollCount > dailyGoal
    val todayProgressFraction = if (dailyGoal > 0) {
        (todayScrollCount.toFloat() / dailyGoal).coerceIn(0f, 1f)
    } else 0f

    val todayColor = if (isTodayExceeded) ColorAlert else ColorTikTokCyan

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_component"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Dashboard Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.dashboard_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.dashboard_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Date / Current Week badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = weeklyReport?.dateRangeLabel ?: "This Week",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // PART 1: TOTAL SCROLL COUNT FOR CURRENT DAY
            // ==========================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("today_scroll_count_card"),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(todayColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.today_scrolls_title).uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$todayScrollCount",
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.testTag("today_scroll_count")
                                )
                                Text(
                                    text = " reels",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 5.dp, start = 4.dp)
                                )
                            }
                        }

                        // Circular Mini Goal Progress
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(58.dp)
                                .testTag("today_goal_progress")
                        ) {
                            CircularProgressIndicator(
                                progress = { todayProgressFraction },
                                modifier = Modifier.size(58.dp),
                                color = todayColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                strokeWidth = 5.dp
                            )
                            val percent = if (dailyGoal > 0) ((todayScrollCount.toFloat() / dailyGoal) * 100).toInt() else 0
                            Text(
                                text = "$percent%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar & limits
                    LinearProgressIndicator(
                        progress = { todayProgressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = todayColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = ColorTikTokPink,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${DateUtils.formatDuration(todayTimeSeconds)} spent",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("today_time_spent")
                            )
                        }

                        // Status badge (Within Limit or Exceeded)
                        val statusText = if (isTodayExceeded) {
                            "${todayScrollCount - dailyGoal} reels over goal"
                        } else {
                            "${dailyGoal - todayScrollCount} remaining of $dailyGoal"
                        }
                        val statusColor = if (isTodayExceeded) ColorAlert else ColorProductive

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusColor.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isTodayExceeded) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = statusText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // PART 2: SUMMARY OF WEEKLY PROGRESS
            // ==========================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_progress_summary"),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header of Weekly Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.weekly_progress_title).uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${weeklyReport?.totalReels ?: 0} reels this week",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("weekly_total_scrolls")
                            )
                        }

                        // Productivity Score / Grade Pill
                        if (weeklyReport != null) {
                            val gradeColor = when (weeklyReport.productivityGrade) {
                                "A+", "A" -> ColorProductive
                                "B" -> MaterialTheme.colorScheme.primary
                                "C" -> ColorWarning
                                else -> ColorAlert
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = gradeColor.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Grade ${weeklyReport.productivityGrade}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = gradeColor
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "• ${weeklyReport.productivityScore}/100",
                                        fontSize = 11.sp,
                                        color = gradeColor
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7-Day Mini Progress Strip
                    val dailyMetrics = weeklyReport?.dailyBreakdown.orEmpty()
                    if (dailyMetrics.isNotEmpty()) {
                        WeeklyMiniProgressStrip(
                            dailyMetrics = dailyMetrics,
                            dailyGoal = dailyGoal,
                            modifier = Modifier.testTag("weekly_mini_chart")
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Key Summary Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Daily Average
                        WeeklyStatPill(
                            label = stringResource(R.string.weekly_avg_label),
                            value = "${String.format("%.1f", weeklyReport?.dailyAverageReels ?: 0.0)}/day",
                            caption = weeklyReport?.dailyAverageTimeFormatted ?: "0m",
                            modifier = Modifier
                                .weight(1f)
                                .testTag("weekly_daily_average")
                        )

                        // Week Total Time
                        WeeklyStatPill(
                            label = "Total Time",
                            value = weeklyReport?.formattedTotalTime ?: "0m",
                            caption = "this week",
                            modifier = Modifier.weight(1f)
                        )

                        // Week-over-week Trend
                        val percentChange = weeklyReport?.percentChangeFromPreviousWeek
                        val hasTrend = percentChange != null
                        val isImproved = (percentChange ?: 0.0) <= 0
                        val trendColor = if (isImproved) ColorProductive else ColorAlert

                        Surface(
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("weekly_trend_indicator"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (hasTrend) trendColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "vs Last Week",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (hasTrend) {
                                        Icon(
                                            imageVector = if (isImproved)
                                                Icons.AutoMirrored.Filled.TrendingDown
                                            else
                                                Icons.AutoMirrored.Filled.TrendingUp,
                                            contentDescription = null,
                                            tint = trendColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        val formattedPercent = String.format("%.1f", kotlin.math.abs(percentChange ?: 0.0))
                                        Text(
                                            text = "${if (isImproved) "-" else "+"}$formattedPercent%",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = trendColor
                                        )
                                    } else {
                                        Text(
                                            text = "Baseline",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                Text(
                                    text = if (isImproved) "Lower usage" else "Higher usage",
                                    fontSize = 10.sp,
                                    color = if (hasTrend) trendColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Optional Navigation Hint to full weekly report
                    if (onViewWeeklyReportClick != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onViewWeeklyReportClick() }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "View Full Weekly Report Breakdown →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyMiniProgressStrip(
    dailyMetrics: List<DailyReelMetric>,
    dailyGoal: Int,
    modifier: Modifier = Modifier
) {
    val maxCount = dailyMetrics.maxOfOrNull { it.reelCount }?.coerceAtLeast(dailyGoal)?.coerceAtLeast(20) ?: 20

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dailyMetrics.forEach { day ->
                    val fraction = (day.reelCount.toFloat() / maxCount).coerceIn(0.08f, 1f)
                    val animatedFraction by animateFloatAsState(
                        targetValue = fraction,
                        animationSpec = tween(durationMillis = 400),
                        label = "stripBar"
                    )

                    val barColor = when {
                        day.isExceedingGoal -> ColorAlert
                        day.isToday -> ColorTikTokCyan
                        day.reelCount > 0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(32.dp)
                    ) {
                        // Micro bar
                        Box(
                            modifier = Modifier
                                .height(38.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(38.dp * animatedFraction)
                                    .width(if (day.isToday) 14.dp else 10.dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(barColor)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Day initial (M, T, W...)
                        Text(
                            text = day.dayLabel.take(1),
                            fontSize = 10.sp,
                            fontWeight = if (day.isToday) FontWeight.Black else FontWeight.Medium,
                            color = if (day.isToday) ColorTikTokCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyStatPill(
    label: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = caption,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
