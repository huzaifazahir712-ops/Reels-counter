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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyReelMetric
import com.example.ui.theme.ColorAlert
import com.example.ui.theme.ColorProductive
import com.example.ui.theme.ColorTikTokCyan
import com.example.ui.theme.ColorTikTokPink
import com.example.ui.theme.ColorWarning
import com.example.util.DateUtils

@Composable
fun WeeklyChartComponent(
    dailyMetrics: List<DailyReelMetric>,
    dailyGoal: Int = 60,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember { mutableStateOf<DailyReelMetric?>(null) }

    val maxCount = remember(dailyMetrics, dailyGoal) {
        val highest = dailyMetrics.maxOfOrNull { it.reelCount } ?: 0
        maxOf(highest, dailyGoal, 20)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Reels Scrolled",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Goal limit: $dailyGoal reels / day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Legend pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reels",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bar Chart Canvas Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dailyMetrics.forEach { metric ->
                        DailyBarItem(
                            metric = metric,
                            maxCount = maxCount,
                            dailyGoal = dailyGoal,
                            isSelected = selectedDay?.dayKey == metric.dayKey,
                            onClick = {
                                selectedDay = if (selectedDay?.dayKey == metric.dayKey) null else metric
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Day Info Banner
            val activeSelected = selectedDay ?: dailyMetrics.find { it.isToday }
            if (activeSelected != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${activeSelected.dayLabel} (${activeSelected.displayDate})",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (activeSelected.isToday) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "TODAY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Estimated time: ${DateUtils.formatDuration(activeSelected.timeSeconds)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${activeSelected.reelCount}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeSelected.isExceedingGoal) ColorAlert else MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " reels",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyBarItem(
    metric: DailyReelMetric,
    maxCount: Int,
    dailyGoal: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heightFraction = if (maxCount > 0) {
        (metric.reelCount.toFloat() / maxCount).coerceIn(0.04f, 1f)
    } else 0.04f

    val animatedHeight by animateFloatAsState(
        targetValue = heightFraction,
        animationSpec = tween(durationMillis = 600),
        label = "barHeight"
    )

    val barColor = when {
        metric.isExceedingGoal -> Brush.verticalGradient(listOf(ColorAlert, ColorTikTokPink))
        metric.isToday -> Brush.verticalGradient(listOf(ColorTikTokCyan, MaterialTheme.colorScheme.primary))
        metric.reelCount > 0 -> Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        )
        else -> Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(horizontal = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Count above bar
        if (metric.reelCount > 0) {
            Text(
                text = "${metric.reelCount}",
                fontSize = 10.sp,
                fontWeight = if (metric.isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (metric.isExceedingGoal) ColorAlert else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Bar pill
        Box(
            modifier = Modifier
                .fillMaxHeight(animatedHeight)
                .width(if (isSelected) 22.dp else 16.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(barColor)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Day label below
        Text(
            text = metric.dayLabel,
            fontSize = 11.sp,
            fontWeight = if (metric.isToday) FontWeight.Bold else FontWeight.Medium,
            color = if (metric.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
