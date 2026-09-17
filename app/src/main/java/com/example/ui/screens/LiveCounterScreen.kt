package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserSettings
import com.example.data.model.WeeklyProductivityReport
import com.example.ui.components.DailyAndWeeklyProgressDashboard
import com.example.ui.components.ReelsSimulatorCard
import com.example.ui.theme.ColorAlert
import com.example.ui.theme.ColorProductive
import com.example.ui.theme.ColorTikTokCyan
import com.example.ui.theme.ColorTikTokPink
import com.example.ui.theme.ColorWarning
import com.example.util.DateUtils

@Composable
fun LiveCounterScreen(
    todayCount: Int,
    totalCount: Int,
    weeklyReport: WeeklyProductivityReport? = null,
    userSettings: UserSettings,
    isServiceActive: Boolean,
    isAccessibilityEnabledInSettings: Boolean,
    isTikTokInForeground: Boolean,
    activeSessionReels: Int,
    onSimulateReel: () -> Unit,
    onSimulateBatch: (Int) -> Unit,
    onOpenAccessibilitySettings: (Context) -> Unit,
    onNavigateToWeeklyReport: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val dailyGoal = userSettings.dailyReelGoal
    val progressFraction = if (dailyGoal > 0) (todayCount.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f
    val isExceeded = todayCount > dailyGoal
    val remaining = (dailyGoal - todayCount).coerceAtLeast(0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // 1. Accessibility Service Status Banner
            AccessibilityStatusBanner(
                isServiceActive = isServiceActive || isAccessibilityEnabledInSettings,
                isTikTokInForeground = isTikTokInForeground,
                onOpenSettings = { onOpenAccessibilitySettings(context) }
            )
        }

        // 2. Daily and Weekly Progress Dashboard Component
        item {
            DailyAndWeeklyProgressDashboard(
                todayScrollCount = todayCount,
                weeklyReport = weeklyReport,
                userSettings = userSettings,
                onViewWeeklyReportClick = onNavigateToWeeklyReport
            )
        }

        // 3. Main Live Reel Counter Hero Card
        item {
            TodayReelsOdometerCard(
                todayCount = todayCount,
                dailyGoal = dailyGoal,
                progressFraction = progressFraction,
                isExceeded = isExceeded,
                remaining = remaining,
                estimatedSecondsPerReel = userSettings.estimatedSecondsPerReel,
                totalLifetimeCount = totalCount
            )
        }

        // 3. Active Foreground Session Pill (if active or testing)
        if (isTikTokInForeground || activeSessionReels > 0) {
            item {
                ActiveSessionBanner(activeSessionReels = activeSessionReels)
            }
        }

        // 4. Interactive Live Simulator
        item {
            ReelsSimulatorCard(
                onReelScrolled = onSimulateReel,
                onBatchScrolled = onSimulateBatch
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AccessibilityStatusBanner(
    isServiceActive: Boolean,
    isTikTokInForeground: Boolean,
    onOpenSettings: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("accessibility_status_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isServiceActive)
                ColorProductive.copy(alpha = 0.12f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isServiceActive) ColorProductive else ColorWarning)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isServiceActive) "TikTok Counter Active" else "Background Tracker Setup",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (isTikTokInForeground) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColorTikTokPink.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "TIKTOK OPEN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ColorTikTokPink,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isServiceActive)
                    "Service is running. When you scroll on TikTok, each reel is detected and logged automatically."
                else
                    "To count reels automatically when browsing TikTok, enable the TikTok Reel Counter in Android Accessibility Settings.",
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isServiceActive) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("open_accessibility_settings_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessibilityNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enable in Android Settings", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayReelsOdometerCard(
    todayCount: Int,
    dailyGoal: Int,
    progressFraction: Float,
    isExceeded: Boolean,
    remaining: Int,
    estimatedSecondsPerReel: Int,
    totalLifetimeCount: Int
) {
    val accentColor = if (isExceeded) ColorAlert else ColorTikTokCyan
    val totalTimeTodaySeconds = (todayCount * estimatedSecondsPerReel).toLong()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_odometer_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "TODAY'S REELS SCROLLED",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Radial Gauge with Large Digits
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(160.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.size(160.dp),
                        color = accentColor,
                        trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        strokeWidth = 12.dp
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$todayCount",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "of $dailyGoal max",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Time lost & remaining pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = ColorTikTokPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${DateUtils.formatDuration(totalTimeTodaySeconds)} today",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isExceeded) ColorAlert.copy(alpha = 0.15f) else ColorProductive.copy(alpha = 0.15f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = if (isExceeded) "Exceeded by ${todayCount - dailyGoal}" else "$remaining remaining",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isExceeded) ColorAlert else ColorProductive,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveSessionBanner(activeSessionReels: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ColorTikTokPink.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(ColorTikTokPink)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Current TikTok Session",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "$activeSessionReels reels in session",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ColorTikTokPink
            )
        }
    }
}
