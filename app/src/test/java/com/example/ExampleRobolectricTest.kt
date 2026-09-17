package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ReelScrollEvent
import com.example.data.model.UserSettings
import com.example.data.util.ProductivityCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ReelScroll Counter", appName)
  }

  @Test
  fun `productivity calculation works for weekly report`() {
    val settings = UserSettings(dailyReelGoal = 50)
    val events = listOf(
      ReelScrollEvent(id = 1, dayKey = "2026-09-16", weekKey = "2026-W38", hourOfDay = 14, dwellTimeSeconds = 30),
      ReelScrollEvent(id = 2, dayKey = "2026-09-16", weekKey = "2026-W38", hourOfDay = 15, dwellTimeSeconds = 45),
      ReelScrollEvent(id = 3, dayKey = "2026-09-16", weekKey = "2026-W38", hourOfDay = 23, dwellTimeSeconds = 25)
    )
    val report = ProductivityCalculator.generateReport(
      weekKey = "2026-W38",
      events = events,
      previousWeekEvents = emptyList(),
      settings = settings
    )
    assertEquals(3, report.totalReels)
    assertNotNull(report.productivityGrade)
    assertTrue(report.productivityScore > 0)
    assertEquals(7, report.dailyBreakdown.size)
  }
}

