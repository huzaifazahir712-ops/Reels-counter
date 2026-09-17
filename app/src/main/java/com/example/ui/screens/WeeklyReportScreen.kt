package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InsightType
import com.example.data.model.ProductivityInsight
import com.example.data.model.UserSettings
import com.example.data.model.WeeklyProductivityReport
import com.example.ui.components.DailyAndWeeklyProgressDashboard
import com.example.ui.components.ProductivityScoreCard
import com.example.ui.components.TimeDistributionComponent
import com.example.ui.components.WeeklyChartComponent
import com.example.ui.theme.ColorAlert
import com.example.ui.theme.ColorProductive
import com.example.ui.theme.ColorWarning

@Composable
fun WeeklyReportScreen(
    report: WeeklyProductivityReport?,
    userSettings: UserSettings,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onCurrentWeek: () -> Unit,
    todayCount: Int = 0,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current

    if (report == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val resolvedTodayCount = if (todayCount > 0) todayCount else (report.dailyBreakdown.find { it.isToday }?.reelCount ?: 0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Week Navigator Header
            WeekNavigatorHeader(
                report = report,
                onPreviousWeek = onPreviousWeek,
                onNextWeek = onNextWeek,
                onCurrentWeek = onCurrentWeek
            )
        }

        // 1. Dashboard: Today's Scrolls & Weekly Progress Summary
        item {
            DailyAndWeeklyProgressDashboard(
                todayScrollCount = resolvedTodayCount,
                weeklyReport = report,
                userSettings = userSettings
            )
        }

        // 2. Hero Productivity Score Card
        item {
            ProductivityScoreCard(report = report)
        }

        // 2. Daily Breakdown Bar Chart
        item {
            WeeklyChartComponent(
                dailyMetrics = report.dailyBreakdown,
                dailyGoal = userSettings.dailyReelGoal
            )
        }

        // 3. Time of Day & Late Night Doomscroll Distribution
        item {
            TimeDistributionComponent(
                stats = report.timeOfDayDistribution,
                totalReels = report.totalReels
            )
        }

        // 4. Personalized Actionable Insights Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Weekly Productivity Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Behavioral analysis and habits based on your reel counts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(report.actionableInsights.size) { index ->
            val insight = report.actionableInsights[index]
            InsightCard(insight = insight)
        }

        // 5. Export / Copy Report Button
        item {
            OutlinedButton(
                onClick = {
                    val summaryText = buildString {
                        appendLine("📊 Weekly TikTok Productivity Report")
                        appendLine("Week: ${report.dateRangeLabel}")
                        appendLine("Total Reels: ${report.totalReels}")
                        appendLine("Time Spent: ${report.formattedTotalTime}")
                        appendLine("Productivity Score: ${report.productivityScore}/100 (Grade ${report.productivityGrade})")
                        appendLine("Daily Average: ${String.format("%.1f", report.dailyAverageReels)} reels/day")
                        appendLine("Peak Period: ${report.timeOfDayDistribution.peakPeriodLabel}")
                        if (report.timeOfDayDistribution.lateNightPercentage > 20) {
                            appendLine("⚠️ Late Night Doomscroll: ${report.timeOfDayDistribution.lateNightPercentage}% of reels")
                        }
                    }
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Weekly Productivity Report", summaryText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Report copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("copy_report_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy Report Summary", fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun WeekNavigatorHeader(
    report: WeeklyProductivityReport,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onCurrentWeek: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousWeek,
                modifier = Modifier.testTag("prev_week_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Week",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = report.dateRangeLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (report.isCurrentWeek) {
                    Text(
                        text = "Current Week (${report.weekKey})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Surface(
                        onClick = onCurrentWeek,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "Jump to Current Week",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onNextWeek,
                enabled = !report.isCurrentWeek,
                modifier = Modifier.testTag("next_week_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Week",
                    tint = if (!report.isCurrentWeek)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun InsightCard(insight: ProductivityInsight) {
    val (cardBg, accentColor, iconVector) = when (insight.type) {
        InsightType.POSITIVE -> Triple(
            ColorProductive.copy(alpha = 0.1f),
            ColorProductive,
            Icons.Default.CheckCircle
        )
        InsightType.WARNING -> Triple(
            ColorAlert.copy(alpha = 0.1f),
            ColorAlert,
            Icons.Default.Warning
        )
        InsightType.TIP -> Triple(
            ColorWarning.copy(alpha = 0.1f),
            ColorWarning,
            Icons.Default.Lightbulb
        )
        InsightType.NEUTRAL -> Triple(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.primary,
            Icons.Default.TrendingDown
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = insight.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = insight.description,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
