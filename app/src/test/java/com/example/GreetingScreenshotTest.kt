package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.DailyReelMetric
import com.example.data.model.TimeOfDayStats
import com.example.data.model.UserSettings
import com.example.data.model.WeeklyProductivityReport
import com.example.ui.components.DailyAndWeeklyProgressDashboard
import com.example.ui.components.ProductivityScoreCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleReport = WeeklyProductivityReport(
      weekKey = "2026-W38",
      dateRangeLabel = "Sep 15 – Sep 21",
      isCurrentWeek = true,
      totalReels = 184,
      totalTimeSeconds = 5520,
      formattedTotalTime = "1h 32m",
      dailyAverageReels = 26.3,
      dailyAverageTimeFormatted = "13m",
      previousWeekTotalReels = 240,
      percentChangeFromPreviousWeek = -23.3,
      productivityScore = 84,
      productivityGrade = "A",
      productivityGradeLabel = "High Focus 🎯",
      dailyBreakdown = emptyList(),
      timeOfDayDistribution = TimeOfDayStats(
        morningReels = 20,
        afternoonReels = 50,
        eveningReels = 90,
        lateNightReels = 24,
        peakPeriodLabel = "Evening",
        lateNightPercentage = 13
      ),
      actionableInsights = emptyList()
    )

    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = true) {
        ProductivityScoreCard(report = sampleReport)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun dashboard_screenshot() {
    val dailyMetrics = listOf(
      DailyReelMetric("2026-09-15", "Mon", "Sep 15", 35, 1050, isToday = false, isExceedingGoal = false),
      DailyReelMetric("2026-09-16", "Tue", "Sep 16", 42, 1260, isToday = true, isExceedingGoal = false),
      DailyReelMetric("2026-09-17", "Wed", "Sep 17", 0, 0, isToday = false, isExceedingGoal = false),
      DailyReelMetric("2026-09-18", "Thu", "Sep 18", 0, 0, isToday = false, isExceedingGoal = false),
      DailyReelMetric("2026-09-19", "Fri", "Sep 19", 0, 0, isToday = false, isExceedingGoal = false),
      DailyReelMetric("2026-09-20", "Sat", "Sep 20", 0, 0, isToday = false, isExceedingGoal = false),
      DailyReelMetric("2026-09-21", "Sun", "Sep 21", 0, 0, isToday = false, isExceedingGoal = false)
    )

    val sampleReport = WeeklyProductivityReport(
      weekKey = "2026-W38",
      dateRangeLabel = "Sep 15 – Sep 21",
      isCurrentWeek = true,
      totalReels = 77,
      totalTimeSeconds = 2310,
      formattedTotalTime = "38m",
      dailyAverageReels = 38.5,
      dailyAverageTimeFormatted = "19m",
      previousWeekTotalReels = 120,
      percentChangeFromPreviousWeek = -35.8,
      productivityScore = 88,
      productivityGrade = "A",
      productivityGradeLabel = "Laser Focus",
      dailyBreakdown = dailyMetrics,
      timeOfDayDistribution = TimeOfDayStats(15, 25, 30, 7, "Evening", 9),
      actionableInsights = emptyList()
    )

    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = true) {
        DailyAndWeeklyProgressDashboard(
          todayScrollCount = 42,
          weeklyReport = sampleReport,
          userSettings = UserSettings(dailyReelGoal = 50)
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/dashboard.png")
  }
}

